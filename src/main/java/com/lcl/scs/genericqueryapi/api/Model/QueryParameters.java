package com.lcl.scs.genericqueryapi.api.Model;

public class QueryParameters {
    String type;
    String value;
    Boolean array;
    
    public QueryParameters(String type, String value, Boolean array) {
        this.type = type;
        this.value = value;
        this.array = array;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getArray() {
        if(array != null) {
            return array;
        }
        else {
            return false;
        }
    }

    public void setArray(Boolean array) {
        this.array = array;
    }

    @Override
    public String toString() {
        if(array != null) {
            return "{type:" + type + ", value:" + value + ", array: "+String.valueOf(array)+"}";
        }
        else {
            return "{type:" + type + ", value:" + value + ", array: false"+"}";
        }
    }
}
