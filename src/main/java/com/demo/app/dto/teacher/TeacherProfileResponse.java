package com.demo.app.dto.teacher;

import com.demo.app.marker.Excelable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileResponse implements Excelable {

    private int id;

    private String username;

    private String fullName;

    private String birthday;

    private String Gender;

    private String phoneNumber;

    private String email;

    private String code;

    private List<String> roles;

}
