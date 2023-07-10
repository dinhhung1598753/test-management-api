package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.question.MultipleQuestionRequest;
import com.demo.app.dto.question.QuestionExcelRequest;
import com.demo.app.dto.question.SingleQuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.exception.FileInputException;
import com.demo.app.model.Answer;
import com.demo.app.model.Question;
import com.demo.app.repository.AnswerRepository;
import com.demo.app.repository.ChapterRepository;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.service.FileStorageService;
import com.demo.app.service.QuestionService;
import com.demo.app.util.excel.ExcelUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final SubjectRepository subjectRepository;

    private final QuestionRepository questionRepository;

    private final ChapterRepository chapterRepository;

    private final AnswerRepository answerRepository;

    private final FileStorageService fileStorageService;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public void saveQuestion(SingleQuestionRequest request, MultipartFile file) throws EntityNotFoundException, IOException {
        var question = mapRequestToQuestion(request);
        var saved = questionRepository.save(question);
        if (file != null) {
            uploadQuestionImage(saved, file);
            questionRepository.save(saved);
        }
    }

    private Question mapRequestToQuestion(SingleQuestionRequest request) {
        @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Chapter with id %d not found !", request.getChapterId()),
                        HttpStatus.NOT_FOUND
                ));
        var question = mapper.map(request, Question.class);
        question.setId(null);
        question.setChapter(chapter);
        question.setAnswers(request.getAnswers().parallelStream()
                .map(answerRequest -> {
                    var answer = mapper.map(answerRequest, Answer.class);
                    answer.setQuestion(question);
                    return answer;
                }).collect(Collectors.toList()));
        return question;
    }

    @Override
    @Transactional
    public void saveAllQuestions(MultipleQuestionRequest request) {
        var subject = subjectRepository.findByCode(request.getSubjectCode())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Subject %s not found !", request.getSubjectCode()),
                        HttpStatus.NOT_FOUND
                ));
        @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findBySubjectAndOrder(subject, request.getChapterNo())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Chapter No %d not found !", request.getChapterNo()),
                        HttpStatus.NOT_FOUND
                ));
        var questions = request.getQuestions()
                .parallelStream()
                .map(questionRequest -> {
                    var question = mapper.map(questionRequest, Question.class);
                    question.setChapter(chapter);
                    question.setAnswers(questionRequest.getAnswers()
                            .parallelStream()
                            .map(answerRequest -> {
                                var answer = mapper.map(answerRequest, Answer.class);
                                answer.setQuestion(question);
                                return answer;
                            }).collect(Collectors.toList()));
                    return question;
                }).collect(Collectors.toList());
        questionRepository.saveAll(questions);
    }

    @Override
    @Transactional
    public void importQuestion(MultipartFile file) throws IOException {
        if (ExcelUtils.notHaveExcelFormat(file)) {
            throw new FileInputException(
                    "There are something wrong with file, please check file format is .xlsx !",
                    HttpStatus.CONFLICT);
        }
        var requests = ExcelUtils.convertExcelToDataTransferObject(file, QuestionExcelRequest.class);
        var questions = requests.parallelStream().map(request -> {
            var subject = subjectRepository.findByCode(request.getSubjectCode())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Subject %s not found !", request.getSubjectCode()),
                            HttpStatus.NOT_FOUND
                    ));
            @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findBySubjectAndOrder(subject, request.getChapterNo())
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Chapter No %d not found !", request.getChapterNo()),
                            HttpStatus.NOT_FOUND
                    ));
            var question = mapper.map(request, Question.class);
            question.setChapter(chapter);
            question.setAnswers(mapRequestsToAnswers(
                    question,
                    request.getAnswer1(),
                    request.getAnswer2(),
                    request.getAnswer3(),
                    request.getAnswer4(),
                    request.getCorrectedAnswer()));
            return question;
        }).collect(Collectors.toList());
        questionRepository.saveAll(questions);
    }

    private List<Answer> mapRequestsToAnswers(Question question,
                                              String answer1, String answer2, String answer3, String answer4,
                                              int correctedAnswer) {
        var answerNumbers = Map.of(
                1, answer1,
                2, answer2,
                3, answer3,
                4, answer4);
        return answerNumbers.entrySet().parallelStream().map(entry -> {
            var answer = Answer.builder()
                    .question(question)
                    .content(entry.getValue())
                    .isCorrected(false)
                    .build();
            if (entry.getKey() == correctedAnswer) {
                answer.setIsCorrected(true);
            }
            return answer;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<QuestionResponse> getAllQuestionsBySubjectCode(String code) {
        var subject = subjectRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subject with code: %s not found !", code),
                        HttpStatus.NOT_FOUND));
        var questions = questionRepository.findByChapterIn(subject.getChapters());
        return questions.parallelStream()
                .map(question -> {
                    var chapter = question.getChapter();
                    var questionResponse = mapper.map(question, QuestionResponse.class);
                    questionResponse.setChapter(mapper.map(chapter, ChapterResponse.class));
                    questionResponse.setSubjectCode(subject.getCode());
                    questionResponse.setSubjectTitle(subject.getTitle());
                    return questionResponse;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateQuestion(int questionId, SingleQuestionRequest request, MultipartFile file) throws IOException {
        @SuppressWarnings("DefaultLocale") var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));
        @SuppressWarnings("DefaultLocale") var chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));

        question.setLevel(Question.Level.valueOf(request.getLevel()));
        question.setTopicText(request.getTopicText());
        question.setChapter(chapter);

        var requestAnswer = request.getAnswers().iterator();
        question.getAnswers().forEach(answer -> {
            var answerId = answer.getId();
            answer = mapper.map(requestAnswer.next(), Answer.class);
            answer.setQuestion(question);
            answer.setId(answerId);
            answerRepository.save(answer);
        });
        if (file != null) {
            uploadQuestionImage(question, file);
        }
        questionRepository.save(question);
    }

    private void uploadQuestionImage(Question question, MultipartFile file) throws IOException {
        fileStorageService.checkIfFileIsImageFormat(Collections.singletonList(file));
        var path = fileStorageService.createClassDirectory("questions/" + question.getId());
        fileStorageService.upload(path, file);
        question.setTopicImage(path);
    }

    @Override
    @Transactional
    public void disableQuestion(int questionId) {
        @SuppressWarnings("DefaultLocale") var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));
        question.setEnabled(false);
        questionRepository.save(question);
    }

}
