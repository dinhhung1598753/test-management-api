package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "student_test_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTestDetail extends BaseEntity {

    @Column(name = "selected_answer", length = 4)
    private String selectedAnswer;

    @ManyToOne
    private StudentTest studentTest;

    @OneToOne
    private TestSetQuestion testSetQuestion;


}
