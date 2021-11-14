package com.ninyo.mongodb.service.repository;

import com.ninyo.mongodb.service.entities.Node;

public interface HierarchyRepositoryCustom {

    Node find(String tenantId, String id);

    Node findNullSafe(String tenantId, String id);

    String attach(String tenantId, String id, String parentId);

    void detach(String tenantId, String id);

    void move(String tenantId, String id, String newParentId);

}