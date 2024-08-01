package org.example.demo1207.repository;

import org.bson.types.ObjectId;
import org.example.demo1207.model.Change;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRepository extends MongoRepository<Change, ObjectId>,
        PagingAndSortingRepository<Change, ObjectId> {

}
