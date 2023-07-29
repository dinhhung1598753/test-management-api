package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "test_set_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSetQuestion extends BaseEntity {

    @Column(name = "question_no")
    private int questionNo;

    @Column(name = "binary_answer")
    private String binaryAnswer;

    @ManyToOne
    private TestSet testSet;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "testSetQuestion", cascade = CascadeType.ALL)
    private List<StudentTestDetail> studentTestDetails;

    @OneToMany(mappedBy = "testSetQuestion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TestSetQuestionAnswer> testSetQuestionAnswers;


}
