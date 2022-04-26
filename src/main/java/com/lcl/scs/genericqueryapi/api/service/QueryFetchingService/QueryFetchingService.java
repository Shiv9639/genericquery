package com.lcl.scs.genericqueryapi.api.service.QueryFetchingService;

import com.lcl.scs.genericqueryapi.api.Model.QueryDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface QueryFetchingService extends MongoRepository<QueryDetails, String>{

    @Query("{query_id:'?0'}")
	QueryDetails findByQueryId(String id);

	public long count();

}