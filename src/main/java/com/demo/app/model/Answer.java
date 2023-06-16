package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer extends BaseEntity {

    @Column(name = "content")
    @Nationalized
    private String content;

    @Column(name = "is_corrected")
    private Boolean isCorrected;

    @ManyToOne
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<TestSetQuestionAnswer> testSetQuestionAnswers;


}
