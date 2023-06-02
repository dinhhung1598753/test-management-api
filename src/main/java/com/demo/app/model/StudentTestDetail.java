package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "student_test_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTestDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "question_mark")
    private double questionMark;

    @Column(name = "selected_answer", length = 4)
    private String selectedAnswer;

    @ManyToOne
    private StudentTest studentTest;

    @OneToOne
    private TestSetQuestion testSetQuestion;


}
