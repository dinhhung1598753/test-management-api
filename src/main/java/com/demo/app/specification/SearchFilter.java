package com.demo.app.specification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SearchFilter {

    private String field;

    private QueryOperator operator;

    private String value;

}
