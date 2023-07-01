package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "student", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"phone_number"}, name = "uni_phone_number"),
        @UniqueConstraint(columnNames = {"code"}, name = "uni_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends Person {

    @Column(name = "course")
    private int course;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<StudentTest> studentTests;

    @Override
    public String toString() {
        return "Student{" +
                "id=" + super.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return Objects.equals(super.getId(), student.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId());
    }
}
