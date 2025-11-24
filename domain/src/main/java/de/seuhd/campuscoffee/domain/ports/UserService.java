package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.model.User;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Service interface for User operations.
 * This is the main entry point for all User-related business logic.
 */
public interface UserService {
    
    /**
     * Clears all users.
     */
    void clear();

    /**
     * Retrieves all users.
     */
    @NonNull List<User> getAll();

    /**
     * Retrieves a user by ID.
     */
    @NonNull User getById(@NonNull Long id);

    /**
     * Retrieves a user by Login Name.
     */
    @NonNull User getByLoginName(@NonNull String loginName);

    /**
     * Creates or updates a user.
     * Checks for duplicates and valid data.
     */
    @NonNull User upsert(@NonNull User user);

    /**
     * Deletes a user by ID.
     */
    void delete(@NonNull Long id);
}