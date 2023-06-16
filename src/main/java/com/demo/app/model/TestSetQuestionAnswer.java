package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "test_set_question_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSetQuestionAnswer extends BaseEntity {

    @Column(name = "answer_no")
    private int answerNo;

    @ManyToOne
    private TestSetQuestion testSetQuestion;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "answer_id")
    private Answer answer;

}
