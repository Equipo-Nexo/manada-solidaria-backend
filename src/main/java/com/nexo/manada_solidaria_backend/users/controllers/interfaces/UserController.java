package com.nexo.manada_solidaria_backend.users.controllers.interfaces;

import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.ProfileResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserDetailResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserProfileResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserPostResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserResponse;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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
    List<Rol> updateRoles(
            @Valid @RequestBody UpdateRolesRequest request,
            @AuthenticationPrincipal User authenticatedUser
    );
}
