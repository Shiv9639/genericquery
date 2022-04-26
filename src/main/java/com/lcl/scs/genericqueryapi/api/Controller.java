package com.lcl.scs.genericqueryapi.api;

import com.lcl.scs.genericqueryapi.api.Model.QueryDetails;

import com.lcl.scs.genericqueryapi.api.Model.APIRequestBody;
import com.lcl.scs.genericqueryapi.api.Model.QueryParameters;
import com.lcl.scs.genericqueryapi.api.config.Constants;
import com.lcl.scs.genericqueryapi.api.service.QueryFetchingService.QueryFetchingService;
import com.lcl.scs.genericqueryapi.api.service.mongo.MongoService;
import com.lcl.scs.genericqueryapi.api.service.ods.OdsService;
import com.lcl.scs.genericqueryapi.api.util.logging.LoggingUtilities;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/generic-query-api")
public class Controller {

    @Autowired
	QueryFetchingService queryRepository;

    @Autowired
    OdsService odsService;
    
    @Autowired
    MongoService mongoService;

    @PostMapping(produces = "application/json")
    public ResponseEntity queryOds(@RequestBody APIRequestBody request) {

        String query_id = request.getQuery_id();
        QueryParameters[] values = request.getQuery_info();

        // get query from the Repository using the query_id
        try {
            QueryDetails queryDetails = queryRepository.findByQueryId(query_id);
            String query = queryDetails.getQuery();
            String db = queryDetails.getDb();

            // List of supported dbs
            if(db.equalsIgnoreCase("ODS")) {
                try {
                    return new ResponseEntity(odsService.executeQuery(query, values), HttpStatus.OK);
                }
                catch(Exception ex) {
                    String response = Constants.errorToResponse(request.toString(), ex.getMessage());
                    LoggingUtilities.generateErrorLog(response);
                    return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
                }
            }
            else if(db.equalsIgnoreCase("mongo")) {
                try {
                    return new ResponseEntity(mongoService.executeQuery(queryDetails, values), HttpStatus.OK);
                }
                catch(Exception ex) {
                    String response = Constants.errorToResponse(request.toString(), ex.getMessage());
                    LoggingUtilities.generateErrorLog(response);
                    return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
                }
            }
            else {
                String response = Constants.errorToResponse(request.toString(), "Database not found.");
                LoggingUtilities.generateErrorLog(response);
                return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
            }
        }
        catch(Exception ex) {
            String response = Constants.errorToResponse(request.toString(), "Invalid query_id or query_info objects");
            LoggingUtilities.generateErrorLog(response);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }
}
