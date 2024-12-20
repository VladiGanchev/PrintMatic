package com.example.printmatic.repository;

import com.example.printmatic.model.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findUserById(Long id);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
//    Optional<UserEntity> findByEmailForUpdate(@Param("email") String email);
}
