package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;

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
public class ExamClass extends BaseEntity{

    @Column(name = "semester")
    private String semester;

    @Column(name = "room_name")
    @Nationalized
    private String roomName;

    @Column(name = "code")
    private String code;

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


}
