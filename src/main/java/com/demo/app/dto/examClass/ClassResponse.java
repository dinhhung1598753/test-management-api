package com.demo.app.dto.examClass;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassResponse {

    private int id;

    private String roomName;

    private String semester;

    private String code;

    private String createdDate;

}
