package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AttributeOverride(
        name = "enabled",
        column = @Column(name = "is_enabled", insertable = false, updatable = false)
)
@MappedSuperclass
public abstract class Person extends BaseEntity {

    @Nationalized
    @Column(name = "full_name")
    private String fullname;

    @Column(name = "code")
    private String code;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private User user;

}
