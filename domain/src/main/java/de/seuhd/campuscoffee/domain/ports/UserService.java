package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.User;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Service interface for User operations.
 * This interface defines the core business logic operations for managing Users.
 * This is a port in the hexagonal architecture pattern, implemented by the domain layer
 * and consumed by the API layer. It encapsulates business rules and orchestrates
 * data operations through the {@link UserDataService} port.
 */
public interface UserService {

    /**
     * Clears all user data.
     */
    void clear();

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all user entities; never null, but may be empty if no users exist
     */
    @NonNull List<User> getAll();

    /**
     * Retrieves a specific user by its unique identifier.
     *
     * @param id the unique identifier of the user to retrieve; must not be null
     * @return the user entity with the specified ID; never null
     */
    @NonNull User getById(@NonNull Long id);

    /**
     * Retrieves a specific user by its name
     *
     * @param name the name of the user to retrieve; must not be null
     * @return the user entity with specified name; never null
     */
    @NonNull User getByName(@NonNull String name);

    /**
     * Delete a User by id
     * @param id User to be deleted
     */
    void deleteUser(@NonNull Long id);

    /**
     * Creates a new User or updates an existing one.
     * This method performs an "upsert" operation:
     * <ul>
     *   <li>If the User has no ID (null), a new User is created</li>
     *   <li>If the User has an ID, and it exists, the existing User is updated</li>
     * </ul>
     * <p>
     * Business rules enforced:
     * <ul>
     *   <li>Usernames must be unique (enforced by database constraint)</li>
     *   <li>All required fields must be present and valid</li>
     *   <li>Timestamps (createdAt, updatedAt) are managed by the {@link UserDataService}.</li>
     * </ul>
     *
     * @param user the User entity to create or update; must not be null
     * @return the persisted User entity with populated ID and timestamps; never null
     * @throws NotFoundException if attempting to update a User that does not exist
     * @throws DuplicationException if a User with the same name already exists
     */
    @NonNull User upsert(@NonNull User user);
}
