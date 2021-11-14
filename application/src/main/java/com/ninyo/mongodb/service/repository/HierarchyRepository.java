package com.ninyo.mongodb.service.repository;

import com.ninyo.mongodb.service.entities.Node;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HierarchyRepository extends MongoRepository<Node, String>, HierarchyRepositoryCustom {

    List<Node> findByTenantIdAndForeignKey(String tenantId, String foreignKey);

    Node findByTenantIdAndId(String tenantId, String id);

    List<Node> findByTenantId(String tenantId);

    List<Node> findByTenantIdAndIdIn(String tenantId, List<String> ids);

    List<Node> findByTenantIdAndType(String tenantId, String type);

    @Query(value = "{'$and' : [ {'tenantId' : ?0}, {'ancestorsIds' : ?1} ] }")
    List<Node> findAllDescendantsByTenantId(String tenantId, String id);

    @Query(value = "{'$and' : [ {'tenantId' : ?0}, {'parentId' : ?1} ] }")
    List<Node> findAllChildrenByTenantId(String tenantId, String id);

    @Query(value = "{'$and' : [ {'tenantId' : ?0}, {'ancestorsIds' : ?1}, {'type' : ?2} ] }")
    List<Node> findAllDescendantsByTenantIdAndType(String tenantId, String id, String type);

    @Query(value = "{'$and' : [ {'tenantId' : ?0}, {'parentId' : ?1}, {'type' : ?2} ] }")
    List<Node> findAllChildrenByTenantIdAndType(String tenantId, String id, String type);

    @Transactional
    void deleteByTenantId(String tenantId);
}
