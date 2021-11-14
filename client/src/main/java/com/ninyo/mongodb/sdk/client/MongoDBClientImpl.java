package com.ninyo.mongodb.sdk.client;

import com.ninyo.common.rest.model.DtoResponse;
import com.ninyo.common.rest.model.IdRequestResponse;
import com.ninyo.common.rest.model.ListResponse;
import com.ninyo.common.rest.model.VersionResponse;
import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.security.secured.client.factory.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MongoDBClientImpl implements MongoDBClient {

    private static final String PING = "/ping";
    private static final String NODES = "/nodes";
    private static final String ID_PATTERN = "/{id}";
    private static final String LIST = "/list";
    private static final String DESCENDANTS = "/descendants";
    private static final String ANCESTORS = "/ancestors";
    private static final String ATTACH = "/attach";
    private static final String DETACH = "/detach";
    private static final String MOVE = "/move";

    private static final Logger logger = LoggerFactory.getLogger(MongoDBClientImpl.class);

    @Autowired
    private RestTemplateFactory restTemplateFactory;

    private RestTemplate rest;

    @Value("${mongodb.service.uri:#{null}}")
    private String uri;

    @Value("${mongodb.server.token.auth.enabled:false}")
    private Boolean clientServerTokenAuthEnabled;

    @Value("${mongodb.server.id:#{null}}")
    private String clientServerId;

    @Value("${mongodb.server.secret:#{null}}")
    private String clientServerSecret;

    @PostConstruct
    protected void init() {
        rest = restTemplateFactory.createRestTemplate(uri, clientServerTokenAuthEnabled, clientServerId, clientServerSecret);
    }

    @Override
    public String ping() {
        logger.info("Pinging to mongodb manager service");
        ResponseEntity<VersionResponse> res = rest.exchange(uri + PING, HttpMethod.GET, null, new ParameterizedTypeReference<VersionResponse>() {
        });
        logger.info(String.format("Response output : %s", res.getBody()));
        logger.info(String.format("Response status code : %s", res.getStatusCode()));
        int value = res.getStatusCode().value();
        return String.valueOf(value);
    }

    @Override
    public IdRequestResponse<String> create(NodeUpsertRequest nodeUpsertRequest) {
        logger.info("Creating node");
        HttpEntity<NodeUpsertRequest> entity = new HttpEntity<>(nodeUpsertRequest);
        ResponseEntity<IdRequestResponse<String>> res = rest.exchange(uri + NODES, HttpMethod.POST, entity, new ParameterizedTypeReference<IdRequestResponse<String>>() {
        });
        return res.getBody();
    }

    @Override
    public DtoResponse<NodeFindResponse> find(String id) {
        logger.info("Finding node with id: " + id);
        ResponseEntity<DtoResponse<NodeFindResponse>> res = rest.exchange(uri + NODES + ID_PATTERN, HttpMethod.GET, null, new ParameterizedTypeReference<DtoResponse<NodeFindResponse>>() {
        }, id);
        return res.getBody();
    }

    @Override
    public ListResponse<NodeFindResponse> findAll() {
        logger.info("Finding all nodes");
        ResponseEntity<ListResponse<NodeFindResponse>> res = rest.exchange(uri + NODES, HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<NodeFindResponse>>() {
        });
        return res.getBody();
    }

    @Override
    public ListResponse<NodeFindResponse> findByType(String type) {
        logger.info("Finding all nodes with type: " + type);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri + NODES).queryParam("type", type);
        ResponseEntity<ListResponse<NodeFindResponse>> res = rest.exchange(builder.build().toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<NodeFindResponse>>() {
        });
        return res.getBody();
    }

    @Override
    public ListResponse<NodeFindResponse> findList(List<String> ids) {
        logger.info("Finding nodes with given ids");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri + NODES + LIST).queryParam("ids", String.join(",", ids));
        ResponseEntity<ListResponse<NodeFindResponse>> res = rest.exchange(builder.build().toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<NodeFindResponse>>() {
        });
        return res.getBody();
    }

    @Override
    public ListResponse<NodeFindResponse> findAllDescendants(String id, String type, boolean firstLevel) {
        logger.info(String.format("Finding all %s of node with id: %s and type: %s", firstLevel ? "children" : "descendants", id, type == null ? "all" : type));

        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("id", id);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri + NODES + ID_PATTERN + DESCENDANTS).queryParam("firstLevel", firstLevel);
        if (type != null) {
            builder.queryParam("type", type);
        }
        ResponseEntity<ListResponse<NodeFindResponse>> res = rest.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<ListResponse<NodeFindResponse>>() {
        });
        return res.getBody();
    }

    @Override
    public List<List<NodeFindResponse>> findAllAncestorsByForeignKeyUnordered(String foreignKey) {
        logger.info("Finding all ancestors of node with foreignKey: " + foreignKey + " unordered");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri + NODES + ANCESTORS).queryParam("foreignKey", foreignKey);
        ResponseEntity<List<List<NodeFindResponse>>> res = rest.exchange(builder.build().toUri(), HttpMethod.GET, null, new ParameterizedTypeReference<List<List<NodeFindResponse>>>() {
        });
        return res.getBody();
    }

    @Override
    public void update(String id, NodeUpsertRequest nodeUpsertRequest) {
        logger.info("Updating with id: " + id);
        HttpEntity<NodeUpsertRequest> entity = new HttpEntity<>(nodeUpsertRequest);
        rest.put(uri + NODES + ID_PATTERN, entity, id);
    }

    @Override
    public IdRequestResponse<String> attach(String id, String parentId) {
        logger.info("Attaching node with id: " + id + " to a parent with id: " + parentId);
        HttpEntity<IdRequestResponse<String>> entity = new HttpEntity<>(IdRequestResponse.<String>builder().id(parentId).build());
        ResponseEntity<IdRequestResponse<String>> res = rest.exchange(uri + NODES + ID_PATTERN + ATTACH, HttpMethod.POST, entity, new ParameterizedTypeReference<IdRequestResponse<String>>() {
        }, id);
        return res.getBody();
    }

    @Override
    public void detach(String id) {
        logger.info("Detaching node with id: " + id);
        rest.exchange(uri + NODES + ID_PATTERN + DETACH, HttpMethod.POST, null, Void.class, id);
    }

    @Override
    public void move(String id, String newParentId) {
        logger.info("Moving node with id: " + id + " to a new parent with id: " + newParentId);
        HttpEntity<IdRequestResponse<String>> entity = new HttpEntity<>(IdRequestResponse.<String>builder().id(newParentId).build());
        rest.exchange(uri + NODES + ID_PATTERN + MOVE, HttpMethod.POST, entity, Void.class, id);
    }

    @Override
    public void delete(String id) {
        logger.info("Deleting node with id: " + id);
        rest.exchange(uri + NODES + ID_PATTERN, HttpMethod.DELETE, null, Void.class, id);
    }

    @Override
    public void deleteAll() {
        logger.info("Deleting all nodes");
        rest.exchange(uri + NODES, HttpMethod.DELETE, null, Void.class);
    }

}
