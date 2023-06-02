package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "student_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_mark")
    private double testMark;

    @Column(name = "image")
    @Lob
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
