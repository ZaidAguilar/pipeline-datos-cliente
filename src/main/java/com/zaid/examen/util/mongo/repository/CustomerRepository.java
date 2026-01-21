package com.zaid.examen.util.mongo.repository;

import com.zaid.examen.util.mongo.model.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerDocument, String> {
}
