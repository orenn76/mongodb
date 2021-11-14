package com.ninyo.mongodb.service.services;

import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContext;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class HierarchyManagerServiceImplIntegrationTest {

    private static final String NODE_ID_1 = "nodeId1";
    private static final String SOURCE_1 = "source1";
    private static final String TENANT_1 = "tenant1";
    private static final String NODE_ID_2 = "nodeId2";
    private static final String SOURCE_2 = "source2";
    private static final String TENANT_2 = "tenant2";
    private static final String FOREIGN_KEY_1 = "1";
    private static final String FOREIGN_KEY_2 = "2";
    private static final String FOREIGN_KEY_3 = "3";
    private static final String FOREIGN_KEY_4 = "4";
    private static final String FOREIGN_KEY_5 = "5";
    private static final String FOREIGN_KEY_6 = "6";
    private static final String TYPE_GROUP = "group";
    private static final String TYPE_DEVICE = "device";

    @Autowired
    private HierarchyManagerService mongoDBService;

    @Before
    @After
    public void tearDown() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        mongoDBService.deleteAll();
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_2).sourceId(SOURCE_2).tenantId(TENANT_2).build());
        mongoDBService.deleteAll();
    }

    @Test
    public void shouldCreateAndFind() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        NodeFindResponse nodeFindResponseFound = mongoDBService.find(id);

        assertNotNull(nodeFindResponseFound);
        assertEquals(FOREIGN_KEY_1, nodeFindResponseFound.getForeignKey());
    }

    @Test
    public void shouldFindAll() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_2).sourceId(SOURCE_2).tenantId(TENANT_2).build());
        mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_5).type(TYPE_DEVICE).build());

        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findAll();
        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(id1, nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(id2, nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindByType() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_DEVICE).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_2).sourceId(SOURCE_2).tenantId(TENANT_2).build());
        mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());

        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findByType(TYPE_DEVICE);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(id2, nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(id3, nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_3, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindList() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_2).sourceId(SOURCE_2).tenantId(TENANT_2).build());
        mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());

        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findList(Arrays.asList(id1, id2));

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(id1, nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(id2, nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllDescendants() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        String id4 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());
        mongoDBService.attach(id2, id1);
        mongoDBService.attach(id3, id2);
        mongoDBService.attach(id4, id2);

        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findAllDescendants(id1, TYPE_DEVICE, false);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(id3, nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_3, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(id4, nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_4, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllDescendantsFirstLevelByTenantIdAndType() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        String id4 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_GROUP).build());
        String id5 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_5).type(TYPE_DEVICE).build());
        String id6 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_6).type(TYPE_DEVICE).build());
        mongoDBService.attach(id2, id1);
        mongoDBService.attach(id3, id2);
        mongoDBService.attach(id4, id1);
        mongoDBService.attach(id5, id1);
        mongoDBService.attach(id6, id1);

        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findAllDescendants(id1, TYPE_DEVICE, true);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(id5, nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_5, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(id6, nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_6, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllAncestorsByForeignKeyUnordered() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        mongoDBService.attach(id2, id1);
        mongoDBService.attach(id3, id2);

        String id4 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_GROUP).build());
        String id5 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_5).type(TYPE_GROUP).build());
        String id6 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        mongoDBService.attach(id5, id4);
        mongoDBService.attach(id6, id5);

        List<List<NodeFindResponse>> nodeFindResponseLists = mongoDBService.findAllAncestorsByForeignKeyUnordered(FOREIGN_KEY_3);

        assertNotNull(nodeFindResponseLists);
        assertEquals(2, nodeFindResponseLists.size());
        assertEquals(2, nodeFindResponseLists.get(0).size());
        assertEquals(2, nodeFindResponseLists.get(1).size());
        List id1id2List = Arrays.asList(id1, id2);
        List id4id5List = Arrays.asList(id4, id5);
        assertTrue(id1id2List.contains(nodeFindResponseLists.get(0).get(0).getId()));
        assertTrue(id1id2List.contains(nodeFindResponseLists.get(0).get(1).getId()));
        assertTrue(id4id5List.contains(nodeFindResponseLists.get(1).get(0).getId()));
        assertTrue(id4id5List.contains(nodeFindResponseLists.get(1).get(1).getId()));
    }

    @Test
    public void shouldUpdate() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());

        mongoDBService.update(id1, NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());

        NodeFindResponse nodeFindResponse1Found = mongoDBService.find(id1);
        assertNotNull(nodeFindResponse1Found);
        assertEquals(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build().getForeignKey(), nodeFindResponse1Found.getForeignKey());
        assertEquals(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build().getType(), nodeFindResponse1Found.getType());
    }

    @Test
    public void shouldAttachAndDetach() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());

        mongoDBService.attach(id2, id1);
        mongoDBService.attach(id3, id2);

        NodeFindResponse node2Found = mongoDBService.find(id2);
        NodeFindResponse node3Found = mongoDBService.find(id3);
        assertEquals(id2, node3Found.getParentId());
        assertTrue(node3Found.getAncestorsIds().contains(id2));
        assertTrue(node3Found.getAncestorsIds().containsAll(node2Found.getAncestorsIds()));

        mongoDBService.detach(id3);

        node3Found = mongoDBService.find(id3);
        assertNull(node3Found.getParentId());
        assertTrue(node3Found.getAncestorsIds().isEmpty());
    }

    @Test
    public void shouldMove() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        String id3 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        mongoDBService.attach(id2, id1);
        mongoDBService.attach(id3, id2);

        mongoDBService.move(id2, id3);

        NodeFindResponse node2Found = mongoDBService.find(id2);
        NodeFindResponse node3Found = mongoDBService.find(id3);
        assertEquals(id3, node2Found.getParentId());
        assertTrue(node2Found.getAncestorsIds().contains(id3));
        assertTrue(node2Found.getAncestorsIds().containsAll(node3Found.getAncestorsIds()));
    }

    @Test
    public void shouldDelete() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        NodeFindResponse nodeFindResponseFound = mongoDBService.find(id);
        assertNotNull(nodeFindResponseFound);

        mongoDBService.delete(id);

        assertNull(mongoDBService.find(id));
    }

    @Test
    public void shouldDeleteAll() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id1 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        String id2 = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        List<NodeFindResponse> nodeFindResponseList = mongoDBService.findAll();
        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());

        mongoDBService.deleteAll();

        assertNull(mongoDBService.find(id1));
        assertNull(mongoDBService.find(id2));
    }

    @Test
    public void shouldNotUpdate() {
        TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
        String id = mongoDBService.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());

        mongoDBService.delete(id);

        updateShouldFail(id);
    }

    private void updateShouldFail(String id) {
        try {
            TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build());
            mongoDBService.update(id, NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
            fail("My method didn't throw expectedException when I expected it to");
        } catch (NoSuchElementException expectedException) {
        }
    }

}