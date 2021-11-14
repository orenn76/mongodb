package com.ninyo.mongodb.service.repository;

import com.ninyo.mongodb.service.entities.Node;
import com.ninyo.mongodb.service.exceptions.HierarchyManagerException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;

public class HierarchyRepositoryImpl implements HierarchyRepositoryCustom {

    @Autowired
    HierarchyRepository nodeRepository;

    @Override
    public Node find(String tenantId, String id) {
        return nodeRepository.findByTenantIdAndId(tenantId, id);
    }

    @Override
    public Node findNullSafe(String tenantId, String id) {
        Node result = nodeRepository.findByTenantIdAndId(tenantId, id);
        if (result == null) {
            throw new NoSuchElementException("Could not find a node with id: " + id);
        }
        return result;
    }

    @Override
    public String attach(String tenantId, String id, String parentId) {
        Node node = nodeRepository.findNullSafe(tenantId, id);

        if (!tenantId.equals(node.getTenantId())) {
            throw new HierarchyManagerException("Trying to attach node with tenant id: " + node.getTenantId() + " to parent with tenant id: " + tenantId);
        }
        if (node.getParentId() == null) {
            return attach(tenantId, node, parentId);
        }
        if (node.getParentId().equals(parentId)) {
            throw new HierarchyManagerException("Node with id: " + id + " is already attached to a parent with id: " + parentId);
        }

        // node already has a parent which is different than the target parentId
        Node newNode = Node.builder().tenantId(tenantId).foreignKey(node.getForeignKey()).type(node.getType()).build();
        return attach(tenantId, newNode, parentId);
    }

    @Override
    public void detach(String tenantId, String id) {
        Node node = nodeRepository.findNullSafe(tenantId, id);
        node.setParentId(null);
        node.removeAncestorsIds();
        nodeRepository.save(node);
    }

    @Override
    public void move(String tenantId, String id, String newParentId) {
        detach(tenantId, id);
        attach(tenantId, id, newParentId);
    }

    private String attach(String tenantId, Node node, String parentId) {
        node.setParentId(parentId);
        node.addAncestorId(parentId);
        List<String> parentAncestorsIds = nodeRepository.findNullSafe(tenantId, parentId).getAncestorsIds();
        if (parentAncestorsIds != null) {
            node.addAncestorsIds(parentAncestorsIds);
        }
        node = nodeRepository.save(node);
        return node.getId();
    }

}
