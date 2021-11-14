package com.ninyo.mongodb.service.entities;

import com.mysema.query.annotations.QueryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@QueryEntity
@Document
public class Node {

    @Id
    private String id;

    private String tenantId;

    private String parentId;

    @Indexed
    private List<String> ancestorsIds;

    private String foreignKey;

    private String type;

    public void addAncestorId(String ancestorId) {
        if (this.ancestorsIds == null) {
            this.ancestorsIds = new ArrayList<>();
        }
        this.ancestorsIds.add(ancestorId);
    }

    public void addAncestorsIds(List<String> ancestorsIds) {
        if (this.ancestorsIds == null) {
            this.ancestorsIds = new ArrayList<>();
        }
        this.ancestorsIds.addAll(ancestorsIds);
    }

    public void removeAncestorId(String ancestorId) {
        if (this.ancestorsIds != null) {
            this.ancestorsIds.remove(ancestorId);
        }
    }

    public void removeAncestorsIds() {
        if (this.ancestorsIds != null) {
            this.ancestorsIds.clear();
        }
    }

}