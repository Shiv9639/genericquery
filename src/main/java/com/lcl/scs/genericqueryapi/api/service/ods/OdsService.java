package com.lcl.scs.genericqueryapi.api.service.ods;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lcl.scs.genericqueryapi.api.Model.QueryParameters;
import com.lcl.scs.genericqueryapi.api.config.Constants;
import com.lcl.scs.genericqueryapi.api.service.ods.OdsService;
import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OdsService {

    // Get the length of an arbitrary array 
    public int getLen(QueryParameters v) throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();

        try {
            switch(v.getType().toLowerCase()) {
                case "string":
                    String[] array = mapper.readValue(v.getValue(), String[].class);
                    return array.length;

                case "int":
                    Integer[] array2 = mapper.readValue(v.getValue(), Integer[].class);
                    return array2.length;

                case "double":
                    Double[] array3 = mapper.readValue(v.getValue(), Double[].class);
                    return array3.length;    

                case "float": 
                    Float[] array4 = mapper.readValue(v.getValue(), Float[].class);
                    return array4.length;

                case "boolean": 
                    Boolean[] array5 = mapper.readValue(v.getValue(), Boolean[].class);
                    return array5.length;

                case "date":
                    String[] array6 = mapper.readValue(v.getValue(), String[].class);
                    return array6.length;

                case "timestamp":
                    String[] array7 = mapper.readValue(v.getValue(), String[].class);
                    return array7.length;

                default:
                    LoggingUtilities.generateErrorLog("Type of value of array in Query info not understood.");
                    throw new Exception("Type of value of array in Query info not understood.");
            }
        }
        catch(Exception ex) {
            LoggingUtilities.generateErrorLog("Mapping query info array to Java array failed", ex);
            throw new Exception("Mapping query info array to Java array failed "+v.toString());
        }
    }

    // Set the Prepared Statement
    public void set(PreparedStatement st, int counter, QueryParameters v) throws Exception {

        switch(v.getType().toLowerCase()) {
            case "string":
                st.setString(counter, v.getValue());
                break;
            case "int":
                st.setInt(counter, Integer.parseInt(v.getValue()));
                break;
            case "double":
                st.setDouble(counter, Double.parseDouble(v.getValue()));
                break;
            case "float": 
                st.setFloat(counter, Float.parseFloat(v.getValue()));
                break;
            case "boolean": 
                st.setBoolean(counter, Boolean.parseBoolean(v.getValue()));
                break;
            case "date":
                st.setDate(counter, Date.valueOf(v.getValue()));
                break;
            case "timestamp":
                st.setTimestamp(counter, Timestamp.valueOf(v.getValue()), Calendar.getInstance(TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE)));
                break;
            default:
                throw new Exception("Type of value in Query info not understood.");
        }
    }

    // set for arrays
    public int setArray(PreparedStatement st, int counter, QueryParameters v) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        try {
            switch(v.getType().toLowerCase()) {
                case "string":
                    String[] array = mapper.readValue(v.getValue(), String[].class);
    
                    for(int j = 0; j < array.length; j++) {
                        st.setString(counter, array[j]);
                        counter++;
                    }
                    return counter;

                case "int":
                    Integer[] array2 = mapper.readValue(v.getValue(), Integer[].class);
        
                    for(int j = 0; j < array2.length; j++) {
                        st.setInt(counter, array2[j]);
                        counter++;
                    }
                    return counter;

                case "double":
                    Double[] array3 = mapper.readValue(v.getValue(), Double[].class);
        
                    for(int j = 0; j < array3.length; j++) {
                        st.setDouble(counter, array3[j]);
                        counter++;
                    }
                    return counter;    

                case "float": 
                    Float[] array4 = mapper.readValue(v.getValue(), Float[].class);
            
                    for(int j = 0; j < array4.length; j++) {
                        st.setFloat(counter, array4[j]);
                        counter++;
                    }
                    return counter;  

                case "boolean": 
                    Boolean[] array5 = mapper.readValue(v.getValue(), Boolean[].class);
            
                    for(int j = 0; j < array5.length; j++) {
                        st.setBoolean(counter, array5[j]);
                        counter++;
                    }
                    return counter;  

                case "date":
                    String[] array6 = mapper.readValue(v.getValue(), String[].class);
            
                    for(int j = 0; j < array6.length; j++) {
                        st.setDate(counter, Date.valueOf(array6[j]));
                        counter++;
                    }
                    return counter;      
                
                case "timestamp":

                    String[] array7 = mapper.readValue(v.getValue(), String[].class);
                
                    for(int j = 0; j < array7.length; j++) {
                        st.setTimestamp(counter, Timestamp.valueOf(array7[j]), Calendar.getInstance(TimeZone.getTimeZone(Constants.DEFAULT_TIMEZONE)));
                        counter++;
                    }
                    return counter;
                    
                default:
                    throw new Exception("Type of value in Query info not understood.");
            }
        }
        catch(Exception ex) {
            LoggingUtilities.generateErrorLog("Setting values of array failed", ex);
            throw new Exception("Setting values of array failed");
        }
    }


    @Autowired
    @Qualifier("odsDataSource")
    DataSource odsDataSource;
    
    public String executeQuery(String query, QueryParameters[] values) throws Exception {

        String response = "ERROR: No Response";
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        LoggingUtilities.generateInfoLog("ODS Request Received with query: "+query);

        try {
            // Connect to ODS
            try {
                conn = odsDataSource.getConnection();
            }
            catch(Exception ex) {
                LoggingUtilities.generateErrorLog("Could not connect to database" + query, ex);
                throw new Exception("Could not connect to database");
            }

            // Set up the Query with question marks
            int totalQuestionMarksAdded = 0; // tracks how many have been added to the original query
            for(int i = 0; i < values.length; i ++) {
                QueryParameters v = values[i];
                if(v.getArray()) {
    
                    int len = getLen(v);
                        
                    int first = query.indexOf("?");    
                    int next = first;
                    for(int j = 0; j < i + totalQuestionMarksAdded; j ++) {
                        next = query.indexOf("?", first + 1);
                        first = next;
                    }
                    totalQuestionMarksAdded += len - 1;

                    // Convert ? -> ?,?
                    StringBuilder sb = new StringBuilder(query);
                    
                    sb.deleteCharAt(next);

                    for(int j = 0; j < len; j++) {
                        sb.insert(next, "?");
                        next++;
                        if(j + 1 < len) {
                            sb.insert(next, ",");
                            next++;
                        }
                    }

                    query = sb.toString();
                }
            }
            st = conn.prepareStatement(query);

            int counter = 1;
            // Format Query with values
            for(int i = 0; i < values.length; i++) {
                QueryParameters v = values[i];

                if(v.getArray()) {
                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        counter = setArray(st, counter, v);
                    }
                    catch(Exception ex) {
                        LoggingUtilities.generateErrorLog("Error while reading array from JSON" + query, ex);
                        throw new Exception("Could not convert JSON string to array");
                    }
                }
                else {
                    try {
                        set(st, counter, v);
                    }
                    catch(Exception ex) {
                        LoggingUtilities.generateErrorLog("Could not understand type" + query, ex);
                        throw new Exception(ex.getMessage());
                    }
                    counter++;

                }
            }
            // Execute Query
            rs = st.executeQuery();
            // Format Response
            ResultSetMetaData meta = rs.getMetaData();
            int numberOfColumns = meta.getColumnCount();

            try {
                ObjectMapper mapper = new ObjectMapper();

                // Create object with two fields: types & records
                ObjectNode main = mapper.createObjectNode();
                ObjectNode listTypes = mapper.createObjectNode();
                
                // Column names
                List<String> columnNames = new ArrayList<String>();  

                for(int i=0; i < numberOfColumns; i++) {
                    
                    // Convert SQL Type to a String for better readability by end user
                    int sqlType = meta.getColumnType(i+1);
                    String type = Constants.typeConvert(sqlType);
                    
                    columnNames.add(meta.getColumnName(i+1));

                    listTypes.put(columnNames.get(i), type);
                }

                main.set("types", listTypes);

                // Values of the records
                ArrayNode records = mapper.createArrayNode();

                int numberOfRecords = 0;
                while(rs.next()) {
                    ObjectNode row = mapper.createObjectNode();

                    for(int i=0; i < numberOfColumns; i++) {
                        row.put(columnNames.get(i), rs.getString(i+1));
                    }

                    records.add(row);
                    numberOfRecords++;
                }

                main.put("number_of_records", numberOfRecords);
                main.set("records", records);
                response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);

                main = mapper.createObjectNode();
            }
            catch(Exception ex) {
                LoggingUtilities.generateErrorLog("Error while mapping response" + query, ex);
                throw new Exception("Could not format response JSON");
            }

            rs.close();

        } catch (Exception ex) {
            LoggingUtilities.generateErrorLog("Error while executing statement/connection" + query, ex);
            String error = (ex.getMessage()).replace("\n","");
            throw new Exception("Query Failed: "+error);

        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
                if (st != null && !st.isClosed()) {
                    st.close();
                }
            } catch (Exception e) {
                LoggingUtilities.generateErrorLog("Error while closing statement/connection" + query, e);
                throw new Exception("Could not close database connection");
            }

        }
        return response;
    }
}
