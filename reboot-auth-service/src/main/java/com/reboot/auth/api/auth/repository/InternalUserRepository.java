package com.reboot.auth.api.auth.repository;

import com.reboot.auth.api.auth.model.entity.InternalUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for {@link InternalUser} entities.
 * Queries are scoped to non-deleted users by convention — callers must filter accordingly.
 */
public interface InternalUserRepository extends JpaRepository<InternalUser, Long> {

    /**
     * Finds an active, non-deleted user by their email address.
     *
     * @param email the email to look up
     * @return the matching user, or empty if none found
     */
    Optional<InternalUser> findByEmailAndDeletedFalse(String email);
}
