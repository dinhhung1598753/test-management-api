package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "subject", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code", name = "uni_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject extends BaseEntity {

    @Column(name = "title")
    @Nationalized
    private String title;

    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "credit")
    private int credit;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Chapter> chapters;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<ExamClass> examClasses;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Test> tests;

}
