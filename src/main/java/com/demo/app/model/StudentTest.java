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

    /** Number of corrected question that student have been answered. */
    @Column(name = "mark")
    private int mark;

    /**
     * Grade is score of corrected answer out of all the question.
     * Grade = mark / all question in test set  * 100.
     * */
    @Column(name = "grade")
    private double grade;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "test_date")
    private LocalDate testDate;

    /**
     * A Student's test have 2 state: In-Process and Finished
     * - In-Process: Students can continue to take the test even if they log out and back in
     * - Finished: The test closed and show the mark/grade
     * */
    @Column(name = "state")
    private String state;

    @ManyToOne
    private Student student;

    @ManyToOne
    private TestSet testSet;

    @OneToMany(mappedBy = "studentTest", cascade = CascadeType.ALL)
    private List<StudentTestDetail> studentTestDetails;

}
