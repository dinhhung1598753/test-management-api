package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

}
