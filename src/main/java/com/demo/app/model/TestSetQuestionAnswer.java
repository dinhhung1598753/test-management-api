package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "test_set_question_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSetQuestionAnswer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "answer_no")
    private int answerNo;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne
    private TestSetQuestion testSetQuestion;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "answer_id")
    private Answer answer;

    @PrePersist
    private void prePersist(){
        enabled = true;
    }
}
