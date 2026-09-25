package com.learnify.backend.repo;

import com.learnify.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Boolean existsByEmailAndDeletedFalse(String email);

    public Boolean existsByUsernameAndDeletedFalse(String username);

    public Optional<User> findByUsernameAndDeletedFalse(String username);

    public Optional<User> findByUserIdAndDeletedFalse(Long id);

    @org.springframework.data.jpa.repository.Query("""
        SELECT u FROM User u
        WHERE u.deleted = false AND u.userRole = com.learnify.backend.enums.UserRole.STUDENT
        ORDER BY u.reputationPoints DESC
    """)
    public List<User> findTopStudentsByReputation();
}
