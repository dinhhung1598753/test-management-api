package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "exam_class", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code", name = "uni_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamClass{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "semester")
    private String semester;

    @Column(name = "room_name")
    @Nationalized
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
    private List<Student> students;

    @ManyToOne
    private Test test;

    @PrePersist
    private void prePersist() {
        createdDate = LocalDate.now();
        enabled = true;
    }

    @Override
    public String toString() {
        return "ExamClass{" +
                "id=" + id +
                ", semester='" + semester + '\'' +
                ", roomName='" + roomName + '\'' +
                ", code='" + code + '\'' +
                ", createdDate=" + createdDate +
                ", enabled=" + enabled +
                '}';
    }
}
