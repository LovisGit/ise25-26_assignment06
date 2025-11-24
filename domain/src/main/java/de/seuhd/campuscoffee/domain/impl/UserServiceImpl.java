package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.ports.UserDataService;
import de.seuhd.campuscoffee.domain.ports.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDataService userDataService;

    @Override
    public void clear() {
        log.warn("Clearing all user data");
        userDataService.clear();
    }

    @Override
    public @NonNull List<User> getAll() {
        log.debug("Retrieving all users");
        return userDataService.getAll();
    }

    @Override
    public @NonNull User getById(@NonNull Long id) {
        log.debug("Retrieving user with ID: {}", id);
        return userDataService.getById(id);
    }

    @Override
    public @NonNull User getByLoginName(@NonNull String loginName) {
        log.debug("Retrieving user with login name: {}", loginName);
        return userDataService.getByLoginName(loginName);
    }

    @Override
    public @NonNull User upsert(@NonNull User user) {
        // Business Logic: Check if we are creating or updating
        if (user.id() == null) {
            log.info("Creating new user: {}", user.loginName());
        } else {
            log.info("Updating user with ID: {}", user.id());
            // Business Rule: You can't update a user that doesn't exist.
            // This check ensures the ID is valid before we try to save.
            Objects.requireNonNull(user.id());
            userDataService.getById(user.id());
        }
        return performUpsert(user);
    }

    @Override
    public void delete(@NonNull Long id) {
        log.info("Trying to delete user with ID: {}", id);
        userDataService.delete(id);
        log.info("Deleted user with ID: {}", id);
    }

    /**
     * Helper method to handle the save operation with consistent logging.
     */
    private @NonNull User performUpsert(@NonNull User user) {
        try {
            User upsertedUser = userDataService.upsert(user);
            log.info("Successfully upserted user with ID: {}", upsertedUser.id());
            return upsertedUser;
        } catch (DuplicationException e) {
            // If the DB complains about a duplicate (e.g. same email), we log it and re-throw.
            log.error("Error upserting user '{}': {}", user.loginName(), e.getMessage());
            throw e;
        }
    }
}
