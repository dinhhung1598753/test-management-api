package com.demo.app.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Integer id;
    private String email;

    private String username;

    private String createdAt;

    private String updatedAt;

    @JsonProperty("isEnabled")
    private String enabled;

    private List<String> roles;

}
