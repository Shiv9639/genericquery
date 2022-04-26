package com.lcl.scs.genericqueryapi.api.Model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="GenericAPIQueries")
public class QueryDetails {

    private String query_id;
    private String query;
    private String db;
    private String description;
    private String collection;
    private String sort;
    private Boolean aggregate;

    public QueryDetails(String query_id, String query, String db, String description, String collection, String sort, Boolean aggregate) {
        this.query_id = query_id;
        this.query = query;
        this.db = db;
        this.description = description;
        this.collection = collection;
        this.sort = sort;
        this.aggregate = aggregate;
    }

    public String getDb() {
        return db;
    }

    public String getQuery() {
        return query;
    }

    public String getCollection() {
        return collection;
    }

    public String getSort() {
        return sort;
    }

    public Boolean getAggregate() {
        return aggregate;
    }

    @Override
    public String toString() {
        if(collection != null) {
            if(sort != null) {
                if(aggregate != null) {
                    return "QueryDetails [db=" + db + ", description=" + description + ", query=" + query + ", query_id=" + query_id + "collection=" + collection + "sort=" + sort
                + "aggregate="+ aggregate + "]";
                }
                else {
                    return "QueryDetails [db=" + db + ", description=" + description + ", query=" + query + ", query_id=" + query_id + "collection=" + collection + "sort=" + sort
                + "]";
                }
            }
            else {
                return "QueryDetails [db=" + db + ", description=" + description + ", query=" + query + ", query_id=" + query_id + "collection=" + collection
                + "]";
            }
        }
        else {
            return "QueryDetails [db=" + db + ", description=" + description + ", query=" + query + ", query_id=" + query_id
                + "]";
        }
    }

}
