package com.demo.app.service.impl;

import com.demo.app.dto.chapter.ChapterResponse;
import com.demo.app.dto.question.QuestionRequest;
import com.demo.app.dto.question.QuestionResponse;
import com.demo.app.exception.EntityNotFoundException;
import com.demo.app.model.Answer;
import com.demo.app.model.Question;
import com.demo.app.repository.QuestionRepository;
import com.demo.app.repository.SubjectRepository;
import com.demo.app.service.QuestionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final SubjectRepository subjectRepository;

    private final QuestionRepository questionRepository;

    private final ModelMapper mapper;

    @Override
    public void addQuestion(QuestionRequest request) throws EntityNotFoundException {
        var subject = subjectRepository.findByCode(request.getSubjectCode()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Not found any subject with code: %s !", request.getSubjectCode()), HttpStatus.NOT_FOUND)
        );
        var chapter = subject.getChapters().get(request.getChapterNo());
        var question = mapper.map(request, Question.class);
        question.setAnswers(
                request.getAnswers()
                        .stream()
                        .map(answerRequest -> {
                            var answer = mapper.map(answerRequest, Answer.class);
                            answer.setQuestion(question);
                            return answer;
                        })
                        .collect(Collectors.toList())
        );
        question.setChapter(chapter);
        question.setTopicImage(request.getQuestionImage());
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public List<QuestionResponse> getAllQuestionsBySubjectCode(String code) {
        var subject = subjectRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Subject with code: %s not found !", code), HttpStatus.NOT_FOUND)
        );
        var questions = new HashSet<Question>();
        subject.getChapters().forEach(chapter -> {
            for (var question : chapter.getQuestions()) {
                if (question.isEnabled()) {
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

    /** Need to fix */
    @Override
    @Transactional
    public void updateQuestion(int questionId, QuestionRequest request) {
        var existedQuestion = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Not found any question with id: %d !", questionId), HttpStatus.NOT_FOUND)
        );

        var question = mapper.map(request, Question.class);
        if (!existedQuestion.equals(question)) {
            existedQuestion.setTopicText(question.getTopicText());
            existedQuestion.setTopicImage(question.getTopicImage());
            existedQuestion.setLevel(question.getLevel());
        }
        questionRepository.save(existedQuestion);

    }

    @Override
    @Transactional
    public void disableQuestion(int questionId) {
        var question = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Not found any question with id: %d !", questionId), HttpStatus.NOT_FOUND)
        );
        question.setEnabled(false);
        questionRepository.save(question);
    }

}
