package com.csye6225.springapi.springmvcrest.repositories;
import com.csye6225.springapi.springmvcrest.Configuration.ReadOnlyRepository;
import com.csye6225.springapi.springmvcrest.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

@ReadOnlyRepository
public interface UserReadOnlyRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
}
