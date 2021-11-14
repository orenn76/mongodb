package com.ninyo.mongodb.sdk.client;

import com.ninyo.common.rest.model.DtoResponse;
import com.ninyo.common.rest.model.IdRequestResponse;
import com.ninyo.common.rest.model.ListResponse;
import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;

import java.util.List;

public interface MongoDBClient {

    String ping();

    IdRequestResponse<String> create(NodeUpsertRequest nodeUpsertRequest);

    DtoResponse<NodeFindResponse> find(String id);

    ListResponse<NodeFindResponse> findAll();

    ListResponse<NodeFindResponse> findByType(String type);

    ListResponse<NodeFindResponse> findList(List<String> ids);

    ListResponse<NodeFindResponse> findAllDescendants(String id, String type, boolean firstLevel);

    List<List<NodeFindResponse>> findAllAncestorsByForeignKeyUnordered(String foreignKey);

    void update(String id, NodeUpsertRequest nodeUpsertRequest);

    /**
     * Attach a node to a parent, if the node already had a parent then a new node with the same data would be created.
     *
     * @param id
     * @param parentId
     * @return node id - the supplied id or a new id in case the node already had a parent.
     */
    IdRequestResponse<String> attach(String id, String parentId);

    void detach(String id);


    void move(String id, String newParentId);

    void delete(String id);

    void deleteAll();

}