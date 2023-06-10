package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "test_day")
    private LocalDate testDay;

    @Column(name = "total_point")
    private double totalPoint;

    @Column(name = "question_quantity")
    private int questionQuantity;

    @Column(name = "duration")
    private int duration;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private Set<TestSet> testSets;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "test_question",
            joinColumns = @JoinColumn(name = "test_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "question_id", referencedColumnName = "id")
    )
    private List<Question> questions;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<ExamClass> examClasses;

    @ManyToOne
    private Subject subject;

    @PrePersist
    private void prePersist(){
        createdAt = LocalDate.now();
        totalPoint = 100.0d;
        enabled = true;
    }

    @PreUpdate
    private void preUpdate(){
        updatedAt = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", testDay=" + testDay +
                ", totalPoint=" + totalPoint +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
