package org.example.demo1207.repository;

import org.example.demo1207.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByFirstName(String firstName);

    Optional<User> findByLastName(String lastName);

    Optional<User> findByEmail(String email);

    Optional<User> findByPassword(String password);

}
