package com.lcl.scs.genericqueryapi.api.service.mongo;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lcl.scs.genericqueryapi.api.Model.QueryDetails;
import com.lcl.scs.genericqueryapi.api.Model.QueryParameters;
import com.lcl.scs.genericqueryapi.api.config.Constants;
import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MongoService {

    @Autowired
    MongoTemplate mongoTemplate;

    public int setArray(String query, StringBuilder sb, int next, QueryParameters v) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        String[] array = mapper.readValue(v.getValue(), String[].class);
    
        for(int j = 0; j < array.length; j++) {
            sb.deleteCharAt(next);
            sb.insert(next, array[j]);
            next = sb.toString().indexOf("?", next + 1);
        }
        return next;
    }

    public String executeQuery(QueryDetails queryDetails, QueryParameters[] values) throws Exception {

        String query = queryDetails.getQuery();
        String collectionName;

        LoggingUtilities.generateInfoLog("MongoDB Request Received with query: "+query);
        
        try {
            collectionName = queryDetails.getCollection();
        }
        catch(Exception ex) {
            LoggingUtilities.generateErrorLog("No collection specified in query object" + query, ex);
            throw new Exception("No collection specified in query object");
        }

        // insert parameters into query
        StringBuilder sb = new StringBuilder(query);

        if(values.length > 0) {
            int next = query.indexOf("?"); 

            for(int j = 0; j < values.length; j ++) {

                QueryParameters v = values[j];
                    
                if(v.getArray()) {
                    // next = setArray(query, sb, next, v);
                    sb.deleteCharAt(next);
                    sb.insert(next, v.getValue()); // works for string, int, double, bool, date
                    next = sb.toString().indexOf("?", next + 1);
                }
                else {
                    sb.deleteCharAt(next);
                    sb.insert(next, v.getValue()); // works for string, int, double, bool, date
                    next = sb.toString().indexOf("?", next + 1);
                }
            }
        }
        query = sb.toString();
        LoggingUtilities.generateInfoLog("Query with Parameters: "+query);

        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        if(queryDetails.getAggregate() != null) {

            AggregateIterable<Document> aggregateDocuments;

            ObjectMapper mapper = new ObjectMapper();
            try {
                String[] aggregateArray = mapper.readValue(query, String[].class);
                List<Bson> bsonList=new ArrayList<Bson>();  
                for (String aggregate : aggregateArray) {
                    Bson bson = BasicDBObject.parse(aggregate);
                    bsonList.add(bson);
                }
                aggregateDocuments = collection.aggregate(bsonList);
            }
            catch(Exception ex) {
                LoggingUtilities.generateErrorLog("Failed to get aggregate array: " + query, ex);
                throw new Exception("Failed to get aggregate array from query");
            }

            try { // Format JSON Response
                ObjectNode main = mapper.createObjectNode();
                ArrayNode records = mapper.createArrayNode();            
    
                int counter = 0;
                for(Document d : aggregateDocuments) {
                    counter++;
    
                    JsonNode node = mapper.readTree(d.toJson());
                    records.add(node);
                }
    
                main.put("number_of_records", counter);
                main.set("records", records);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
            }
            catch (Exception ex) {
                LoggingUtilities.generateErrorLog("Could not format JSON Response "+ex.getMessage());
                return "Could not format JSON Response "+ex.getMessage();
            }
        }
        else {

            FindIterable<Document> documents;

            if(queryDetails.getSort() != null) {
                Bson bson = BasicDBObject.parse(query);
    
                String sort = queryDetails.getSort();
                Bson sortBson = BasicDBObject.parse(sort);
                documents = collection.find(bson).sort(sortBson);
            }
            else {
                Bson bson = BasicDBObject.parse(query);
    
                documents = collection.find(bson);
            }

            try { // Format JSON Response
                ObjectMapper mapper = new ObjectMapper();
    
                // Create object with two fields: types & records
                ObjectNode main = mapper.createObjectNode();
                ArrayNode records = mapper.createArrayNode();            
    
                int counter = 0;
                for(Document d : documents) {
                    counter++;
    
                    JsonNode node = mapper.readTree(d.toJson());
                    records.add(node);
                }
    
                main.put("number_of_records", counter);
                main.set("records", records);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
            }
            catch (Exception ex) {
                LoggingUtilities.generateErrorLog("Could not format JSON Response "+ex.getMessage());
                return "Could not format JSON Response "+ex.getMessage();
            }
        }

    }
    
}
