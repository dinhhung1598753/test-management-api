package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.util.List;

@Entity
@Table(name = "test_set")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Builder
public class TestSet extends BaseEntity {


    @Column(name = "test_no")
    private int testNo;

    @ManyToOne
    private Test test;

    @OneToMany(mappedBy = "testSet", cascade = CascadeType.ALL)
    private List<TestSetQuestion> testSetQuestions;

    @OneToMany(mappedBy = "testSet", cascade = CascadeType.ALL)
    private List<StudentTest> studentTests;


}
