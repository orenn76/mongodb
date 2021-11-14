package com.ninyo.mongodb.service.mappers;

import com.ninyo.common.rest.mappers.Converter;
import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.service.entities.Node;
import org.springframework.stereotype.Component;

@Component
public class NodeToNodeFindResponseConverter implements Converter<Node, NodeFindResponse> {

    @Override
    public NodeFindResponse convert(Node source) {
        return NodeFindResponse.builder()
                .id(source.getId())
                .parentId(source.getParentId())
                .ancestorsIds(source.getAncestorsIds())
                .foreignKey(source.getForeignKey())
                .type(source.getType())
                .build();
    }

}
