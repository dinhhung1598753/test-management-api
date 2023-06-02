package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "exam_class", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code", name = "uni_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamClass implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "semester")
    private String semester;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "code")
    private String code;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    private Subject subject;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "student_exam_class",
            joinColumns = @JoinColumn(name = "exam_class_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
    private Set<Student> students;

    @ManyToOne
    private Test test;

    @PrePersist
    private void prePersist() {
        createdDate = LocalDate.now();
        enabled = true;
    }

}
