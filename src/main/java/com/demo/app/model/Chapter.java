package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "chapter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter extends BaseEntity {

    @Column(name = "title")
    @Nationalized
    private String title;

    @Column(name = "[order]")
    private int order;

    @ManyToOne(fetch = FetchType.EAGER)
    private Subject subject;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Question> questions;

}
