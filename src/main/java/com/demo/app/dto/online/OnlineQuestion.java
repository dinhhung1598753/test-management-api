package com.demo.app.dto.online;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnlineQuestion {

    private int questionNo;

    private List<OnlineAnswer> answers;

}
