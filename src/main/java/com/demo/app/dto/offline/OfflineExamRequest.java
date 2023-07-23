package com.demo.app.dto.offline;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OfflineExamRequest {

    private String classCode;

    private String studentCode;

    private Integer testNo;

    private List<OfflineExamRequest.OfflineAnswer> answers;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class OfflineAnswer {

        private Integer questionNo;

        private String isSelected;

        private Boolean isCorrected;

    }

}
