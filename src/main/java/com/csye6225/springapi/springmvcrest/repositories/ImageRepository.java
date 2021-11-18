package com.csye6225.springapi.springmvcrest.repositories;

import com.csye6225.springapi.springmvcrest.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Profile, UUID>
{
    Profile findByUserid(String userid);

    @Modifying
    @Transactional
    @Query("delete from Profile  where userid = :userId")
    void deleteByUserid(@Param(value = "userId") String userid);
}
