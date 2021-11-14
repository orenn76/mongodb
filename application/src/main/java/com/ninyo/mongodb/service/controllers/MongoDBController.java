package com.ninyo.mongodb.service.controllers;

import com.ninyo.common.rest.model.DtoResponse;
import com.ninyo.common.rest.model.IdRequestResponse;
import com.ninyo.common.rest.model.ListResponse;
import com.ninyo.common.rest.model.VersionResponse;
import com.ninyo.mongodb.common.NodeFindResponse;
import com.ninyo.mongodb.common.NodeUpsertRequest;
import com.ninyo.mongodb.service.services.HierarchyManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MongoDBController {

    private static final String NODES = "/nodes";
    private static final String ID_PATTERN = "/{id}";
    private static final String LIST = "/list";
    private static final String DESCENDANTS = "/descendants";
    private static final String ANCESTORS = "/ancestors";
    private static final String ATTACH = "/attach";
    private static final String DETACH = "/detach";
    private static final String MOVE = "/move";

    @Autowired
    private HierarchyManagerService service;

    @Value("${mongodb.service.version}")
    private String version;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public VersionResponse ping() {
        return new VersionResponse(false, HttpStatus.OK, "PONG", version);
    }

    @RequestMapping(value = NODES, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdRequestResponse<String> create(@RequestBody NodeUpsertRequest nodeUpsertRequest) {
        return new IdRequestResponse<>(service.create(nodeUpsertRequest));
    }

    @RequestMapping(value = NODES + ID_PATTERN, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public DtoResponse<NodeFindResponse> find(@PathVariable String id) {
        NodeFindResponse nodeFindResponse = service.find(id);
        return new DtoResponse<>(nodeFindResponse);
    }

    @RequestMapping(value = NODES, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ListResponse<NodeFindResponse> findAll(@RequestParam(required = false) String type) {
        List<NodeFindResponse> nodeFindResponseList = (type == null) ? service.findAll() : service.findByType(type);
        return new ListResponse<>(nodeFindResponseList);
    }

    @RequestMapping(value = NODES + LIST, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ListResponse<NodeFindResponse> findList(@RequestParam List<String> ids) {
        List<NodeFindResponse> nodeFindResponseList = service.findList(ids);
        return new ListResponse<>(nodeFindResponseList);
    }

    @RequestMapping(value = NODES + ID_PATTERN + DESCENDANTS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ListResponse<NodeFindResponse> findAllDescendants(@PathVariable String id, @RequestParam(required = false) String type, @RequestParam(required = false) boolean firstLevel) {
        List<NodeFindResponse> nodeFindResponseList = service.findAllDescendants(id, type, firstLevel);
        return new ListResponse<>(nodeFindResponseList);
    }

    @RequestMapping(value = NODES + ANCESTORS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<List<NodeFindResponse>> findAllAncestorsByForeignKeyUnordered(@RequestParam String foreignKey) {
        return service.findAllAncestorsByForeignKeyUnordered(foreignKey);
    }

    @RequestMapping(value = NODES + ID_PATTERN, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String id, @RequestBody NodeUpsertRequest nodeUpsertRequest) {
        service.update(id, nodeUpsertRequest);
    }

    @RequestMapping(value = NODES + ID_PATTERN + ATTACH, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public IdRequestResponse<String> attach(@PathVariable String id, @RequestBody @Validated IdRequestResponse<String> parentId) {
        return new IdRequestResponse<>(service.attach(id, parentId.getId()));
    }

    @RequestMapping(value = NODES + ID_PATTERN + DETACH, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void detach(@PathVariable String id) {
        service.detach(id);
    }

    @RequestMapping(value = NODES + ID_PATTERN + MOVE, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void move(@PathVariable String id, @RequestBody @Validated IdRequestResponse<String> newParentId) {
        service.move(id, newParentId.getId());
    }

    @RequestMapping(value = NODES + ID_PATTERN, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @RequestMapping(value = NODES, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        service.deleteAll();
    }

}