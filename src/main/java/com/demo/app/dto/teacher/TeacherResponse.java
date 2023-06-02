package com.demo.app.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherResponse {

    private int id;

    private String username;

    private String fullName;

    private String birthday;

    private String Gender;

    private String phoneNumber;

    private String email;

}
