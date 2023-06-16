package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Question extends BaseEntity {

    @Column(name = "topic_text")
    @Nationalized
    private String topicText;

    @Lob
    @Column(name = "topic_image", length = 100000)
    private String topicImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @ManyToOne(fetch = FetchType.EAGER)
    private Chapter chapter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public enum Level {
        EASY,
        MEDIUM,
        HARD
    }
}
