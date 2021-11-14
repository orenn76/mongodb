package com.ninyo.common.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenieResponse {

    private boolean hasError;
    private HttpStatus code;
    private String message;

}
