package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.question.SingleQuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.Answer;
import com.demo.app.model.Question;
import com.demo.app.repository.AnswerRepository;
import com.demo.app.repository.ChapterRepository;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final SubjectRepository subjectRepository;

    private final QuestionRepository questionRepository;

    private final ChapterRepository chapterRepository;

    private final AnswerRepository answerRepository;

    private final ModelMapper mapper;

    @Override
    public void addQuestion(SingleQuestionRequest request) throws EntityNotFoundException {
        questionRepository.save(mapRequestToQuestion(request));
    }

    @Override
    @Transactional
    public void addAllQuestions(List<SingleQuestionRequest> requests) throws EntityNotFoundException {
        var questions = requests.parallelStream()
                .map(this::mapRequestToQuestion)
                .collect(Collectors.toList());
        questionRepository.saveAll(questions);
    }

    private Question mapRequestToQuestion(SingleQuestionRequest request) {
        var chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Chapter with id %d not found !", request.getChapterId()),
                        HttpStatus.NOT_FOUND
                ));
        var question = mapper.map(request, Question.class);
        question.setChapter(chapter);
        question.setAnswers(request.getAnswers().parallelStream()
                .map(answerRequest -> {
                    var answer = mapper.map(answerRequest, Answer.class);
                    answer.setQuestion(question);
                    return answer;})
                .collect(Collectors.toList()));
        return question;
    }

    @Override
    @Transactional
    public List<QuestionResponse> getAllQuestionsBySubjectCode(String code) {
        var subject = subjectRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subject with code: %s not found !", code),
                        HttpStatus.NOT_FOUND));
        var questions = new HashSet<Question>();
        subject.getChapters().forEach(chapter -> {
            for (var question : chapter.getQuestions()) {
                if (question.getEnabled()) {
                    questions.add(question);
                }
            }
        });
        return questions.stream()
                .map(question -> {
                    var chapter = question.getChapter();
                    var questionResponse = mapper.map(question, QuestionResponse.class);
                    questionResponse.setChapter(mapper.map(chapter, ChapterResponse.class));
                    questionResponse.setSubjectCode(subject.getCode());
                    questionResponse.setSubjectTitle(subject.getTitle());
                    return questionResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateQuestion(int questionId, SingleQuestionRequest request) {
        var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));
        var chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));

        question.setLevel(Question.Level.valueOf(request.getLevel()));
        question.setTopicText(request.getTopicText());
        question.setTopicImage(request.getTopicImage());
        question.setChapter(chapter);

        var requestAnswer = request.getAnswers().iterator();
        question.getAnswers().forEach(answer -> {
            var answerId = answer.getId();
            answer = mapper.map(requestAnswer.next(), Answer.class);
            answer.setQuestion(question);
            answer.setId(answerId);
            answerRepository.save(answer);
        });
        questionRepository.save(question);

    }

    @Override
    @Transactional
    @CrossOrigin(allowedHeaders = "*", origins = "*")
    public void disableQuestion(int questionId) {
        var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Not found any question with id: %d !", questionId),
                        HttpStatus.NOT_FOUND));
        question.setEnabled(false);
        questionRepository.save(question);
    }

}
