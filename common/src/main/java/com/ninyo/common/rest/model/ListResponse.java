package com.ninyo.common.rest.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonPropertyOrder({ "metaData", "group" })
@Data
@NoArgsConstructor
public class ListResponse<D> extends DtoResponse<D> {

    MetaData metaData;

    public ListResponse(List<D> data) {
        int size = data.size();
        this.metaData = new MetaData(size);
        this.data = data;
    }

    public ListResponse(long count, List<D> data) {
        super();
        this.metaData = new MetaData(count);
        this.data = data;
    }

    @Data
    public static class MetaData {

        //The total count of items
        long count;

        public MetaData() {
            this.count = 0;
        }

        public MetaData(long count) {
            this.count = count;
        }

    }
}
