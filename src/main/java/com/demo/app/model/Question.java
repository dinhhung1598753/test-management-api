package com.demo.app.model;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "topic_text")
    @EqualsAndHashCode.Include
    private String topicText;

    @Lob
    @Column(name = "topic_image", length = 100000)
    @EqualsAndHashCode.Include
    private byte[] topicImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    @EqualsAndHashCode.Include
    private Level level;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_enabled")
    private boolean enabled;

    @ManyToOne(fetch = FetchType.EAGER)
    private Chapter chapter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public enum Level {
        EASY,
        MEDIUM,
        DIFFICULT
    }

    @PrePersist
    private void prePersist() {
        createdDate = LocalDate.now();
        enabled = true;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", topicText='" + topicText + '\'' +
                ", topicImage=" + Arrays.toString(topicImage) +
                ", level=" + level +
                ", createdDate=" + createdDate +
                ", enabled=" + enabled +
                ", answers=" + answers +
                '}';
    }
}
