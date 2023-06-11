package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "test_set")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class TestSet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_no")
    private int testNo;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne
    private Test test;

    @OneToMany(mappedBy = "testSet", cascade = CascadeType.ALL)
    private List<TestSetQuestion> testSetQuestions;

    @OneToMany(mappedBy = "testSet", cascade = CascadeType.ALL)
    private List<StudentTest> studentTests;

    @PrePersist
    private void prePersist(){
        enabled = true;
        createdAt = LocalDate.now();
    }
    @PreUpdate
    private void preUpdate(){
        updatedAt = LocalDate.now();
    }
}
