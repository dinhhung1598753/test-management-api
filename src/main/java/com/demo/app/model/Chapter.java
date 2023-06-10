package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "chapter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    @Nationalized
    private String title;

    @Column(name = "[order]")
    private int order;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    private Subject subject;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Question> questions;

    @PrePersist
    private void prePersist(){
        enabled = true;
    }
}
