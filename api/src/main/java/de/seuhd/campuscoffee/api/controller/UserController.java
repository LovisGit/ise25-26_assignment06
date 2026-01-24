package de.seuhd.campuscoffee.api.controller;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import de.seuhd.campuscoffee.api.exceptions.ErrorResponse;
import de.seuhd.campuscoffee.api.mapper.UserDtoMapper;
import de.seuhd.campuscoffee.domain.ports.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.seuhd.campuscoffee.api.util.ControllerUtils.getLocation;

/**
 * Controller for handling User-related API requests.
 */
@Tag(name = "Users", description = "Operations related to user management.")
@Controller
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Operation(
            summary = "Get all Users.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "array", implementation = UserDto.class)
                            ),
                            description = "All Users as a JSON array."
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(
                userService.getAll().stream()
                        .map(userDtoMapper::fromDomain)
                        .toList()
        );
    }

    @Operation(
            summary = "Get User based on id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "Returns User with queried id"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No User with queried id"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                userDtoMapper.fromDomain(userService.getById(id))
        );
    }

    @Operation(
            summary = "Get User by Name",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "Returns User with queried name"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No User with queried name"
                    )
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<UserDto> filterByName(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(
                userDtoMapper.fromDomain(userService.getByName(name))
        );
    }

    @Operation(
            summary = "Create a new User.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "The created User."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "Invalid User data provided."
                    )
            }
    )
    @PostMapping("")
    public ResponseEntity<UserDto> createUser(
            @RequestBody UserDto userDto
    ) {
        UserDto createdUser = upsert(userDto);
        return ResponseEntity
                .created(getLocation(createdUser.id()))
                .body(createdUser);
    }

    @Operation(
            summary = "Create or update a User by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            ),
                            description = "Upserted User."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "Invalid User data provided."
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> upsertUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto
    ) {
        if (!id.equals(userDto.id())) {
            throw new IllegalArgumentException("User ID in path and body do not match.");
        }
        return ResponseEntity.ok(
                upsert(userDto)
        );
    }

    @Operation(
            summary = "Delete a User by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted User."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ),
                            description = "No User with given id found."
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id); // throws NotFoundException if no User with the provided ID exists
        return ResponseEntity.noContent().build();
    }

    /**
     * Common upsert logic for create and update.
     * @param userDto User to be upserted with changed values
     * @return UserDto with upserted values
     */
    private UserDto upsert(UserDto userDto) {
        return userDtoMapper.fromDomain(
                userService.upsert(
                        userDtoMapper.toDomain(userDto)
                )
        );
    }
}
