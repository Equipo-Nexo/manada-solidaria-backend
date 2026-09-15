package com.nexo.manada_solidaria_backend.users.controllers.interfaces;

import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.*;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequestMapping("/users")
public interface UserController {

    @GetMapping("/{userId}")
    UserDetailResponse getUser(
            @PathVariable UUID userId
    );

    @GetMapping("/{userId}/profile")
    UserProfileResponse getUserProfile(
            @PathVariable UUID userId
    );

    @GetMapping
    List<UserResponse> getUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Rol role
    );

    @GetMapping("/posts")
    List<UserPostResponse> getUserPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String type
    );

    @PutMapping("/profile")
    ProfileResponse updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User authenticatedUser
    );

    @PatchMapping("/roles")
    Set<Rol> updateRoles(
            @Valid @RequestBody UpdateRolesRequest request,
            @AuthenticationPrincipal User authenticatedUser
    );
}
