package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "teacher", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone_number", name = "uni_phone_number"),
        @UniqueConstraint(columnNames = "code", name = "uni_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher extends Person {

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<ExamClass> examClasses;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id"))
    private List<Subject> subjects;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private User user;
}
