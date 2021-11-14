package com.ninyo.mongodb.service.repository;

import com.ninyo.mongodb.service.entities.Node;
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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class HierarchyRepositoryImplIntegrationTest {

    private static final String TENANT_1 = "tenant1";
    private static final String TENANT_2 = "tenant2";
    private static final String FOREIGN_KEY_1 = "1";
    private static final String FOREIGN_KEY_2 = "2";
    private static final String FOREIGN_KEY_3 = "3";
    private static final String FOREIGN_KEY_4 = "4";
    private static final String TYPE_GROUP = "group";
    private static final String TYPE_DEVICE = "device";
    private static final String DUMMY_ID = "dummy_id";

    @Autowired
    private HierarchyRepository nodeRepository;

    @Before
    @After
    public void tearDown() {
        nodeRepository.deleteAll();
    }

    @Test
    public void shouldFind() {
        Node node = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        Node nodeFound = nodeRepository.find(TENANT_1, node.getId());
        assertEquals(node.getForeignKey(), nodeFound.getForeignKey());
    }

    @Test
    public void shouldFindNullSafe() {
        Node node = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        Node nodeFound = nodeRepository.findNullSafe(TENANT_1, node.getId());
        assertEquals(node.getForeignKey(), nodeFound.getForeignKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFindNullSafeThrowException() {
        nodeRepository.findNullSafe(TENANT_1, DUMMY_ID);
    }

    @Test
    public void shouldAttachAndDetach() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).parentId(DUMMY_ID).ancestorsIds(Collections.singletonList(DUMMY_ID)).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).build());

        nodeRepository.attach(TENANT_1, node2.getId(), node1.getId());

        Node node2Found = nodeRepository.findNullSafe(TENANT_1, node2.getId());
        assertEquals(node1.getId(), node2Found.getParentId());
        assertTrue(node2Found.getAncestorsIds().contains(node1.getId()));
        assertTrue(node2Found.getAncestorsIds().containsAll(node1.getAncestorsIds()));

        nodeRepository.detach(TENANT_1, node2.getId());

        node2Found = nodeRepository.findNullSafe(TENANT_1, node2.getId());
        assertNull(node2Found.getParentId());
        assertTrue(node2Found.getAncestorsIds().isEmpty());
    }

    @Test
    public void shouldMove() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).build());
        Node node3 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_3).parentId(DUMMY_ID).ancestorsIds(Collections.singletonList(DUMMY_ID)).build());
        nodeRepository.attach(TENANT_1, node2.getId(), node1.getId());

        nodeRepository.move(TENANT_1, node2.getId(), node3.getId());

        Node node2Found = nodeRepository.findNullSafe(TENANT_1, node2.getId());
        assertEquals(node3.getId(), node2Found.getParentId());
        assertTrue(node2Found.getAncestorsIds().contains(node3.getId()));
        assertTrue(node2Found.getAncestorsIds().containsAll(node3.getAncestorsIds()));
    }

    @Test
    public void shouldFindByTenantIdAndForeignKey() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).build());

        List<Node> nodeFindResponseList = nodeRepository.findByTenantIdAndForeignKey(TENANT_1,  FOREIGN_KEY_1);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(node1.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals( node2.getId(), nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindByTenantIdAndId() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());

        Node nodeFindResponse = nodeRepository.findByTenantIdAndId(TENANT_1,  node1.getId());

        assertNotNull(nodeFindResponse);
        assertEquals(node1.getId(), nodeFindResponse.getId());
    }

    @Test
    public void shouldFindByTenantId() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_2).foreignKey(FOREIGN_KEY_3).build());

        List<Node> nodeFindResponseList = nodeRepository.findByTenantId(TENANT_1);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(node1.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(node2.getId(), nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindByTenantIdAndIdIn() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_3).type(TYPE_GROUP).build());

        List<Node> nodeFindResponseList = nodeRepository.findByTenantIdAndIdIn(TENANT_1,  Arrays.asList(node1.getId(), node2.getId()));

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(node1.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(node2.getId(), nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindByType() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());

        List<Node> nodeFindResponseList = nodeRepository.findByTenantIdAndType(TENANT_1, TYPE_GROUP);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(node1.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_1, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(node2.getId(), nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_2, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldFindAllDescendants() {
        Node node1 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).type(TYPE_GROUP).build());
        Node node2 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).type(TYPE_GROUP).build());
        Node node3 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_3).type(TYPE_DEVICE).build());
        Node node4 = nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_4).type(TYPE_DEVICE).build());
        nodeRepository.attach(TENANT_1, node2.getId(), node1.getId());
        nodeRepository.attach(TENANT_1, node3.getId(), node2.getId());
        nodeRepository.attach(TENANT_1, node4.getId(), node2.getId());

        List<Node> nodeFindResponseList = nodeRepository.findAllDescendantsByTenantIdAndType(TENANT_1, node1.getId(), TYPE_DEVICE);

        assertNotNull(nodeFindResponseList);
        assertEquals(2, nodeFindResponseList.size());
        assertEquals(node3.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_3, nodeFindResponseList.get(0).getForeignKey());
        assertEquals(node4.getId(), nodeFindResponseList.get(1).getId());
        assertEquals(FOREIGN_KEY_4, nodeFindResponseList.get(1).getForeignKey());
    }

    @Test
    public void shouldDeleteByTenantId() {
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_1).build());
        nodeRepository.save(Node.builder().tenantId(TENANT_1).foreignKey(FOREIGN_KEY_2).build());
        Node node3 = nodeRepository.save(Node.builder().tenantId(TENANT_2).foreignKey(FOREIGN_KEY_3).build());

        nodeRepository.deleteByTenantId(TENANT_1);

        List<Node> nodeFindResponseList = nodeRepository.findAll();
        assertNotNull(nodeFindResponseList);
        assertEquals(1, nodeFindResponseList.size());
        assertEquals(node3.getId(), nodeFindResponseList.get(0).getId());
        assertEquals(FOREIGN_KEY_3, nodeFindResponseList.get(0).getForeignKey());
    }

}