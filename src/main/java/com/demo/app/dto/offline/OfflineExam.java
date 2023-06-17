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
public class OfflineExam {

    private String classCode;

    private String studentCode;

    private int testNo;

    private List<OfflineAnswer> answers;
}
