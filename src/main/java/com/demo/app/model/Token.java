package com.demo.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "token")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token extends BaseEntity {

    @Column(name = "token")
    private String token;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "is_expired")
    private boolean expired;

    @Column(name = "is_revoked")
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public enum TokenType{
        BEARER,
        OTP
    }
}
