package com.ninyo.common.rest.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class DtoResponse<DTO> {

    List<DTO> data;

    public DtoResponse(DTO dto) {
        if (dto != null) {
            this.data = Collections.singletonList(dto);
        } else {
            this.data = Collections.emptyList();
        }
    }

}
