package com.example.mongoserver.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.mongoserver.model.Server;

@Repository
public interface ServerRepository extends MongoRepository<Server,String>
{
    
}
