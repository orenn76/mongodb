package com.ninyo.mongodb.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeUpsertRequest {

    private String parentId;
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