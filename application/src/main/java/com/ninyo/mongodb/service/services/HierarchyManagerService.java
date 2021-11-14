package com.ninyo.mongodb.service.services;

import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;

import java.util.List;

public interface HierarchyManagerService {

    String create(NodeUpsertRequest nodeUpsertRequest);

    NodeFindResponse find(String id);

    List<NodeFindResponse> findAll();

    List<NodeFindResponse> findByType(String type);

    List<NodeFindResponse> findList(List<String> ids);

    List<NodeFindResponse> findAllDescendants(String id, String type, boolean firstLevel);

    List<List<NodeFindResponse>> findAllAncestorsByForeignKeyUnordered(String foreignKey);

    void update(String id, NodeUpsertRequest nodeUpsertRequest);

    String attach(String id, String parentId);

    void detach(String id);

    void move(String id, String newParentId);

    void delete(String id);

    void deleteAll();
}
