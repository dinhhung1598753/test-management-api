package com.demo.app.dto.offline;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OfflineAnswer {

    private int questionNo;

    private String selected;

}
