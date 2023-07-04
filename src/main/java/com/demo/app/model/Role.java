package com.demo.app.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(name = "role_name", length = 20)
    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    public enum RoleType{
        ROLE_ADMIN,
        ROLE_PRINCIPAL,
        ROLE_TEACHER,
        ROLE_STUDENT,
        ROLE_USER
    }

    public Role(RoleType roleName) {
        this.roleName = roleName;
    }
}
