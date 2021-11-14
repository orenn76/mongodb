package com.ninyo.common.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class VersionResponse extends GenieResponse {

    private String version;

    public VersionResponse(boolean hasError, HttpStatus code, String description, String version) {
        super(hasError, code, description);
        this.version = version;
    }
}
