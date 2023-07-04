package com.demo.app.dto.student;

import com.demo.app.marker.Excelable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse implements Excelable {

    private Integer id;

    private String username;

    private String fullName;

    private Integer course;

    private String birthday;

    private String gender;

    private String phoneNumber;

    private String email;

    private String code;

    private String createdAt;

    private List<String> roles;
}
