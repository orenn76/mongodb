package com.ninyo.mongodb.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeFindResponse {

    private String id;

    private String parentId;

    private List<String> ancestorsIds;

    private String foreignKey;

    private String type;

}