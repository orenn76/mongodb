package com.ninyo.mongodb.service.mappers;

import com.ninyo.common.rest.mappers.Mapper;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.mongodb.service.entities.Node;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContext;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContextHolder;
import org.springframework.stereotype.Component;

@Component
public class NodeUpsertRequestToNodeMapper implements Mapper<NodeUpsertRequest, Node> {

    @Override
    public void map(NodeUpsertRequest source, Node target) {
        TenantContext tenantContext = TenantContextHolder.INSTANCE.get();
        target.setTenantId(tenantContext.getTenantId());
        target.setParentId(source.getParentId());
        target.setAncestorsIds(source.getAncestorsIds());
        target.setForeignKey(source.getForeignKey());
        target.setType(source.getType());
    }

}
