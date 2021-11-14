package com.ninyo.mongodb.sdk.client;

import com.ninyo.common.rest.model.DtoResponse;
import com.ninyo.common.rest.model.IdRequestResponse;
import com.ninyo.common.rest.model.ListResponse;
import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.mongodb.sdk.MongoDBTestApplication;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContext;
import com.ninyo.security.secured.server.secured.common.tenant.TenantContextHolder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDBTestApplication.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class MongoDBClientImplIntegrationTest {

    private static final String NODE_ID_1 = "nodeId1";
    private static final String SOURCE_1 = "source1";
    private static final String TENANT_1 = "tenant1";
    private static final String NODE_ID_2 = "nodeId2";
    private static final String SOURCE_2 = "source2";
    private static final String TENANT_2 = "tenant2";
    private static final String OK = "200";
    private static final String FOREIGN_KEY_1 = "1";
    private static final String FOREIGN_KEY_2 = "2";
    private static final String FOREIGN_KEY_3 = "3";
    private static final String FOREIGN_KEY_4 = "4";
    private static final String FOREIGN_KEY_5 = "5";
    private static final String FOREIGN_KEY_6 = "6";
    private static final String TYPE_GROUP = "group";
    private static final String TYPE_DEVICE = "device";
    private static TenantContext tenantContext1 = TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(TENANT_1).build();
    private static TenantContext tenantContext2 = TenantContext.builder().nodeId(NODE_ID_2).sourceId(SOURCE_2).tenantId(TENANT_2).build();

    @Autowired
    private MongoDBClient client;

    @Before
    @After
    public void setUpTearDown() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        client.deleteAll();
        TenantContextHolder.INSTANCE.set(tenantContext2);
        client.deleteAll();
        TenantContextHolder.INSTANCE.remove();
    }

    @Test
    public void shouldPing() {
        String statusCode = client.ping();
        assertEquals(OK, statusCode);
    }

    @Test
    public void shouldCreateAndFind() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        DtoResponse<NodeFindResponse> dtoResponse = client.find(idResponse.getId());

        assertNotNull(dtoResponse.getData());
        NodeFindResponse nodeFindResponseFound = dtoResponse.getData().get(0);
        assertEquals(FOREIGN_KEY_1, nodeFindResponseFound.getForeignKey());
    }

    @Test
    public void shouldFindAll() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse3 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        TenantContextHolder.INSTANCE.set(tenantContext2);
        client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());

        TenantContextHolder.INSTANCE.set(tenantContext1);
        ListResponse<NodeFindResponse> res = client.findAll();

        assertNotNull(res.getData());
        Assert.assertEquals(3, res.getData().size());
        assertEquals(idResponse1.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_1, res.getData().get(0).getForeignKey());
        assertEquals(idResponse2.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_2, res.getData().get(1).getForeignKey());
        assertEquals(idResponse3.getId(), res.getData().get(2).getId());
        assertEquals(FOREIGN_KEY_3, res.getData().get(2).getForeignKey());
    }

    @Test
    public void shouldFindByType() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());

        ListResponse<NodeFindResponse> res = client.findByType(TYPE_GROUP);

        assertNotNull(res.getData());
        Assert.assertEquals(2, res.getData().size());
        assertEquals(idResponse1.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_1, res.getData().get(0).getForeignKey());
        assertEquals(idResponse2.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_2, res.getData().get(1).getForeignKey());
    }

    @Test
    public void shouldFindList() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());

        ListResponse<NodeFindResponse> res = client.findList(Arrays.asList(idResponse1.getId(), idResponse2.getId()));

        assertNotNull(res.getData());
        Assert.assertEquals(2, res.getData().size());
        assertEquals(idResponse1.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_1, res.getData().get(0).getForeignKey());
        assertEquals(idResponse2.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_2, res.getData().get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllDescendants() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse3 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        IdRequestResponse<String> idResponse4 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());

        client.attach(idResponse2.getId(), idResponse1.getId());
        client.attach(idResponse3.getId(), idResponse2.getId());
        client.attach(idResponse4.getId(), idResponse2.getId());

        ListResponse<NodeFindResponse> res = client.findAllDescendants(idResponse1.getId(), TYPE_DEVICE, false);

        assertNotNull(res.getData());
        Assert.assertEquals(2, res.getData().size());
        assertEquals(idResponse3.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_3, res.getData().get(0).getForeignKey());
        assertEquals(idResponse4.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_4, res.getData().get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllChildren() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse3 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        IdRequestResponse<String> idResponse4 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse5 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_5).type(TYPE_DEVICE).build());
        IdRequestResponse<String> idResponse6 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_6).type(TYPE_DEVICE).build());

        client.attach(idResponse2.getId(), idResponse1.getId());
        client.attach(idResponse3.getId(), idResponse2.getId());
        client.attach(idResponse4.getId(), idResponse1.getId());
        client.attach(idResponse5.getId(), idResponse1.getId());
        client.attach(idResponse6.getId(), idResponse1.getId());

        ListResponse<NodeFindResponse> res = client.findAllDescendants(idResponse1.getId(), TYPE_DEVICE, true);

        assertNotNull(res.getData());
        Assert.assertEquals(2, res.getData().size());
        assertEquals(idResponse5.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_5, res.getData().get(0).getForeignKey());
        assertEquals(idResponse6.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_6, res.getData().get(1).getForeignKey());

        res = client.findAllDescendants(idResponse1.getId(), null, true);

        assertNotNull(res.getData());
        Assert.assertEquals(4, res.getData().size());
        assertEquals(idResponse2.getId(), res.getData().get(0).getId());
        assertEquals(FOREIGN_KEY_2, res.getData().get(0).getForeignKey());
        assertEquals(idResponse4.getId(), res.getData().get(1).getId());
        assertEquals(FOREIGN_KEY_4, res.getData().get(1).getForeignKey());
        assertEquals(idResponse5.getId(), res.getData().get(2).getId());
        assertEquals(FOREIGN_KEY_5, res.getData().get(2).getForeignKey());
        assertEquals(idResponse6.getId(), res.getData().get(3).getId());
        assertEquals(FOREIGN_KEY_6, res.getData().get(3).getForeignKey());
    }

    @Test
    public void shouldFindAllAncestorsByForeignKeyUnordered() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse3 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        client.attach(idResponse2.getId(), idResponse1.getId());
        client.attach(idResponse3.getId(), idResponse2.getId());

        IdRequestResponse<String> idResponse4 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_4).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse5 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_5).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse6 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        client.attach(idResponse5.getId(), idResponse4.getId());
        client.attach(idResponse6.getId(), idResponse5.getId());

        List<List<NodeFindResponse>> nodeFindResponseLists = client.findAllAncestorsByForeignKeyUnordered(FOREIGN_KEY_3);

        assertNotNull(nodeFindResponseLists);
        assertEquals(2, nodeFindResponseLists.size());
        assertEquals(2, nodeFindResponseLists.get(0).size());
        assertEquals(2, nodeFindResponseLists.get(1).size());
        List id1id2List = Arrays.asList(idResponse1.getId(), idResponse2.getId());
        List id4id5List = Arrays.asList(idResponse4.getId(), idResponse5.getId());
        assertTrue(id1id2List.contains(nodeFindResponseLists.get(0).get(0).getId()));
        assertTrue(id1id2List.contains(nodeFindResponseLists.get(0).get(1).getId()));
        assertTrue(id4id5List.contains(nodeFindResponseLists.get(1).get(0).getId()));
        assertTrue(id4id5List.contains(nodeFindResponseLists.get(1).get(1).getId()));
    }

    @Test
    public void shouldUpdate() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());

        client.update(idResponse1.getId(), NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());

        NodeFindResponse nodeFindResponse1Found = client.find(idResponse1.getId()).getData().get(0);
        assertEquals(FOREIGN_KEY_2, nodeFindResponse1Found.getForeignKey());
    }

    @Test
    public void shouldAttachAndDetach() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());

        client.attach(idResponse2.getId(), idResponse1.getId());

        NodeFindResponse nodeFindResponse2Found = client.find(idResponse2.getId()).getData().get(0);

        assertEquals(idResponse1.getId(), nodeFindResponse2Found.getParentId());
        assertTrue(nodeFindResponse2Found.getAncestorsIds().contains(idResponse1.getId()));

        client.detach(idResponse2.getId());

        nodeFindResponse2Found = client.find(idResponse2.getId()).getData().get(0);
        assertNull(nodeFindResponse2Found.getParentId());
        assertTrue(nodeFindResponse2Found.getAncestorsIds().isEmpty());
    }

    @Test
    public void shouldMove() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_DEVICE).build());
        IdRequestResponse<String> idResponse3 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_GROUP).build());

        client.attach(idResponse2.getId(), idResponse1.getId());

        client.move(idResponse2.getId(), idResponse3.getId());

        NodeFindResponse nodeFindResponse2Found = client.find(idResponse2.getId()).getData().get(0);
        assertEquals(idResponse3.getId(), nodeFindResponse2Found.getParentId());
    }

    @Test
    public void shouldDelete() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());

        client.delete(idResponse1.getId());

        assertTrue(client.find(idResponse1.getId()).getData().isEmpty());
    }

    @Test
    public void shouldDeleteAll() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        IdRequestResponse<String> idResponse2 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_2).type(TYPE_DEVICE).build());

        client.deleteAll();

        assertTrue(client.find(idResponse1.getId()).getData().isEmpty());
        assertTrue(client.find(idResponse2.getId()).getData().isEmpty());
    }

    @Test
    public void updateOfUnExistedNodeShouldThrowAnException() {
        TenantContextHolder.INSTANCE.set(tenantContext1);
        IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        client.delete(idResponse1.getId());

        try {
            client.update(idResponse1.getId(), NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
            fail("Update didn't throw an exception");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        } catch (Exception e) {
            fail("Update threw an unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void createWithMissingTenantIdShouldThrowAnException() {
        try {
            TenantContextHolder.INSTANCE.set(TenantContext.builder().nodeId(NODE_ID_1).sourceId(SOURCE_1).tenantId(null).build());
            client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
            fail("Create didn't throw an exception");
        } catch (HttpServerErrorException e) {
        } catch (Exception e) {
            fail("Create threw an unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void attachToItselfShouldThrowAnException() {
        try {
            TenantContextHolder.INSTANCE.set(tenantContext1);
            IdRequestResponse<String> idResponse1 = client.create(NodeUpsertRequest.builder().foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
            client.attach(idResponse1.getId(), idResponse1.getId());
            fail("Create didn't throw an exception");
        } catch (HttpClientErrorException e) {
        } catch (Exception e) {
            fail("Create threw an unexpected exception: " + e.getMessage());
        }
    }

}