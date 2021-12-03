package com.csye6225.springapi.springmvcrest.repositories;
import com.csye6225.springapi.springmvcrest.Configuration.ReadOnlyRepository;
import com.csye6225.springapi.springmvcrest.domain.User;
import org.springframework.data.repository.Repository;

@ReadOnlyRepository
public interface UserReadOnlyRepository extends Repository {
    User findByUsername(String username);
}
