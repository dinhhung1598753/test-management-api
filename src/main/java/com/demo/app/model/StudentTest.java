package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "student_test")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTest{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mark")
    private int mark;

    @Column(name = "grade")
    private double grade;

    @Lob
    @Column(name = "image")
    private String image;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "test_date")
    private LocalDate testDate;

    @ManyToOne
    private Student student;

    @ManyToOne
    private TestSet testSet;

    @OneToMany(mappedBy = "studentTest", cascade = CascadeType.ALL)
    private List<StudentTestDetail> studentTestDetails;

}
