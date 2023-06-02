package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content")
    @Nationalized
    private String content;

    @Column(name = "is_corrected")
    private boolean isCorrected;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<TestSetQuestionAnswer> testSetQuestionAnswers;

    @PrePersist
    private void prePersist() {
        enabled = true;
    }

}
