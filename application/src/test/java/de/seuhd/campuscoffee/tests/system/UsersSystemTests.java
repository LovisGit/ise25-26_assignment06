package de.seuhd.campuscoffee.tests.system;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

import static de.seuhd.campuscoffee.tests.SystemTestUtils.Requests.userRequests;
import static org.assertj.core.api.Assertions.assertThat;

public class UsersSystemTests extends AbstractSysTest {

    @Test
    void createUser() {
        User userToCreate = TestFixtures.getUserListForInsertion().getFirst();
        User createdUser = userDtoMapper.toDomain(userRequests.create(List.of(userDtoMapper.fromDomain(userToCreate))).getFirst());

        assertEqualsIgnoringIdAndTimestamps(createdUser, userToCreate);
    }

    @Test
    void updateUser() {
        User userToCreate = TestFixtures.getUserListForInsertion().getFirst();
        User createdUser = userDtoMapper.toDomain(userRequests.create(List.of(userDtoMapper.fromDomain(userToCreate))).getFirst());

        User userWithUpdate = new User(
                createdUser.id(),
                createdUser.createdAt(),
                createdUser.updatedAt(),
                createdUser.loginName(),
                createdUser.emailAddress(),
                createdUser.firstName() + "updated",
                createdUser.lastName()
        );

        User updatedUser = userDtoMapper.toDomain(userRequests.update(List.of(userDtoMapper.fromDomain(userWithUpdate))).getFirst());

        assertEqualsIgnoringTimestamps(userWithUpdate, updatedUser);
    }

    @Test
    void deleteUser() {
        List<User> createdUserList = TestFixtures.createUsers(userService);
        User userToDelete = createdUserList.getFirst();
        Objects.requireNonNull(userToDelete.id());

        List<Integer> statusCodes = userRequests.deleteAndReturnStatusCodes(List.of(userToDelete.id(), userToDelete.id()));

        // first deletion should return 204 No Content, second should return 404 Not Found
        assertThat(statusCodes)
                .containsExactly(HttpStatus.NO_CONTENT.value(), HttpStatus.NOT_FOUND.value());

        List<Long> remainingUserIds = userRequests.retrieveAll()
                .stream()
                .map(UserDto::id)
                .toList();
        assertThat(remainingUserIds)
                .doesNotContain(userToDelete.id());
    }
}