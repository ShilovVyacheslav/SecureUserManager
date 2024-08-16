package org.example.demo1207.repository;

import org.bson.types.ObjectId;
import org.example.demo1207.model.View;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewRepository extends MongoRepository<View, ObjectId>,
        PagingAndSortingRepository<View, ObjectId> {

}
