package com.lcl.scs.genericqueryapi.api.Model;

import java.util.Arrays;

public class APIRequestBody {
    String query_id; // Location of query in Mongo DB
    QueryParameters[] query_info; // All the values of the query

    public String getQuery_id() {
        return query_id;
    }

    public void setQuery_id(String query_id) {
        this.query_id = query_id;
    }

    public QueryParameters[] getQuery_info() {
        return query_info;
    }

    public void setQuery_info(QueryParameters[] query_info) {
        this.query_info = query_info;
    }

    @Override
    public String toString() {
        return "{query_id:" + query_id + ", query_info:" + Arrays.toString(query_info) + "}";
    }
}
