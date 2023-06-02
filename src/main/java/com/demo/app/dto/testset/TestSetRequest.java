package com.demo.app.dto.testset;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestSetRequest {

    @Min(value = 1, message = "Test set quantity must be greater than 1 !")
    private int testSetQuantity;

}
