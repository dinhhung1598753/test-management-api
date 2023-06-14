package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "test_set_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSetQuestion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "question_no")
    private int questionNo;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne
    private TestSet testSet;

    @ManyToOne
    private Question question;

    @OneToOne(mappedBy = "testSetQuestion", cascade = CascadeType.ALL)
    private StudentTestDetail studentTestDetail;

    @OneToMany(mappedBy = "testSetQuestion", cascade = CascadeType.ALL)
    private List<TestSetQuestionAnswer> testSetQuestionAnswers;

    @PrePersist
    private void prePersist(){
        enabled = true;
    }

    @PreUpdate
    public void preUpdate(){

    }
}
