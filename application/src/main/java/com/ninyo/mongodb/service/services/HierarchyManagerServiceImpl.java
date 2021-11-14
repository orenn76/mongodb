package com.ninyo.mongodb.service.services;

import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.mongodb.service.entities.Node;
import com.ninyo.mongodb.service.exceptions.HierarchyManagerException;
import com.ninyo.mongodb.service.mappers.NodeToNodeFindResponseConverter;
import com.ninyo.mongodb.service.mappers.NodeUpsertRequestToNodeMapper;
import com.ninyo.mongodb.service.repository.HierarchyRepository;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContext;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HierarchyManagerServiceImpl implements HierarchyManagerService {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyManagerServiceImpl.class);

    @Autowired
    protected HierarchyRepository repository;

    @Autowired
    protected NodeUpsertRequestToNodeMapper nodeUpsertRequestToNodeMapper;

    @Autowired
    protected NodeToNodeFindResponseConverter nodeToNodeFindResponseConverter;

    @Override
    public String create(NodeUpsertRequest nodeUpsertRequest) {
        logger.info("Start creating node");
        Node node = Node.builder().build();
        nodeUpsertRequestToNodeMapper.map(nodeUpsertRequest, node);
        node = repository.save(node);
        logger.info(String.format("Created node with id: %s", node.getId()));
        return node.getId();
    }

    @Override
    public NodeFindResponse find(String id) {
        logger.info(String.format("Start finding node with id: %s", id));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        Node node = repository.find(tenantContext.getTenantId(), id);
        if (node == null) {
            logger.info(String.format("Could not find node with id: %s", id));
            return null;
        }
        logger.info(String.format("Found node with id: %s", id));
        return nodeToNodeFindResponseConverter.convert(node);
    }

    @Override
    public List<NodeFindResponse> findAll() {
        logger.info("Start finding entities");
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        Iterable<Node> nodes = repository.findByTenantId(tenantContext.getTenantId());
        List<NodeFindResponse> nodeFindResponseList = nodeToNodeFindResponseConverter.convert(nodes);
        logger.info(String.format("Found entities of size: %s", nodeFindResponseList.size()));
        return nodeFindResponseList;
    }

    @Override
    public List<NodeFindResponse> findByType(String type) {
        logger.info(String.format("Start finding entities with type: %s", type));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        Iterable<Node> nodes = repository.findByTenantIdAndType(tenantContext.getTenantId(), type);
        List<NodeFindResponse> nodeFindResponseList = nodeToNodeFindResponseConverter.convert(nodes);
        logger.info(String.format("Found entities of size: %s with type: %s", nodeFindResponseList.size(), type));
        return nodeFindResponseList;
    }

    @Override
    public List<NodeFindResponse> findList(List<String> ids) {
        logger.info("Start finding entities");
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        Iterable<Node> nodes = repository.findByTenantIdAndIdIn(tenantContext.getTenantId(), ids);
        List<NodeFindResponse> nodeFindResponseList = nodeToNodeFindResponseConverter.convert(nodes);
        logger.info(String.format("Found entities of size: %s", nodeFindResponseList.size()));
        return nodeFindResponseList;
    }

    @Override
    public List<NodeFindResponse> findAllDescendants(String id, String type, boolean firstLevel) {
        logger.info(String.format("Start finding %s of node with id: %s and type: %s", firstLevel ? "children" : "descendants", id, type == null ? "all" : type));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        List<Node> nodes;
        if (type == null) {
            nodes = firstLevel ? repository.findAllChildrenByTenantId(tenantContext.getTenantId(), id) : repository.findAllDescendantsByTenantId(tenantContext.getTenantId(), id);
        } else {
            nodes = firstLevel ? repository.findAllChildrenByTenantIdAndType(tenantContext.getTenantId(), id, type) : repository.findAllDescendantsByTenantIdAndType(tenantContext.getTenantId(), id, type);
        }
        List<NodeFindResponse> nodeFindResponseList = nodeToNodeFindResponseConverter.convert(nodes);
        logger.info(String.format("Found %s of node with id: %s and type: %s", firstLevel ? "children" : "descendants", id, type));
        return nodeFindResponseList;
    }

    @Override
    public List<List<NodeFindResponse>> findAllAncestorsByForeignKeyUnordered(String foreignKey) {
        logger.info(String.format("Start finding all ancestors of node with foreignKey: %s", foreignKey));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        List<Node> nodes = repository.findByTenantIdAndForeignKey(tenantContext.getTenantId(), foreignKey);
        List<List<NodeFindResponse>> allAncestors = nodes.stream().map(node -> node.getAncestorsIds() == null ? new ArrayList<NodeFindResponse>() : findList(node.getAncestorsIds())).collect(Collectors.toList());
        logger.info(String.format("Found all ancestors of node with foreignKey: %s", foreignKey));
        return allAncestors;
    }

    @Override
    public void update(String id, NodeUpsertRequest nodeUpsertRequest) {
        logger.info(String.format("Start updating node with id: %s", id));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        Node node = repository.findNullSafe(tenantContext.getTenantId(), id);
        nodeUpsertRequestToNodeMapper.map(nodeUpsertRequest, node);
        repository.save(node);
        logger.info(String.format("Updated node with id: %s", id));
    }

    @Override
    public String attach(String id, String parentId) {
        logger.info(String.format("Start attaching node with id: %s to node with id: %s", id, parentId));
        if (id.equals(parentId)) {
            logger.error(String.format("Could not attach a node to itself"));
            throw new HierarchyManagerException("Could not attach a node to itself");
        }
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        String resultId = repository.attach(tenantContext.getTenantId(), id, parentId);
        logger.info(String.format("Attached node with id: %s to node with id: %s", id, parentId));
        return resultId;
    }

    @Override
    public void detach(String id) {
        logger.info(String.format("Start detaching node with id: %s", id));
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        repository.detach(tenantContext.getTenantId(), id);
        logger.info(String.format("Detached node with id: %s", id));
    }

    @Override
    public void move(String id, String newParentId) {
        logger.info(String.format("Start moving node with id: %s to node with id: %s", id, newParentId));
        if (id.equals(newParentId)) {
            logger.error(String.format("Could not move a node to itself"));
            throw new HierarchyManagerException("Could not move a node to itself");
        }
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        repository.move(tenantContext.getTenantId(), id, newParentId);
        logger.info(String.format("Moved node with id: %s to node with id: %s", id, newParentId));
    }

    @Override
    public void delete(String id) {
        logger.info(String.format("Start deleting node with id: %s", id));
        repository.delete(id);
        logger.info(String.format("Deleted node with id: %s", id));
    }

    @Override
    public void deleteAll() {
        logger.info("Start deleting all nodes");
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        repository.deleteByTenantId(tenantContext.getTenantId());
        logger.info("Deleted all nodes");
    }

}
