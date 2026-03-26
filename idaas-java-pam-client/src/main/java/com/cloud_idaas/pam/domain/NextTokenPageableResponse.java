package com.cloud_idaas.pam.domain;

import java.io.Serializable;
import java.util.List;

public class NextTokenPageableResponse<T> implements Serializable {

    private static final long serialVersionUID = 3706388637975771027L;

    private long totalCount;

    private String nextToken;

    private long maxResults;

    private List<T> entities;

    public NextTokenPageableResponse() {
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    public long getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(long maxResults) {
        this.maxResults = maxResults;
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }
}
