package com.nexo.manada_solidaria_backend.users.services.interfaces;

import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.ProfileResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserPostResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserResponse;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    @Override
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    User getUserById(UUID userId);

    void createUser(CreateUserRequest createUserRequest);

    List<UserResponse> getUsers(String username, Rol role);

    List<UserPostResponse> getUserPosts(User user, String type);

    ProfileResponse updateProfile(UpdateProfileRequest request, User authenticatedUser);

    List<Rol> updateRoles(UpdateRolesRequest request, User authenticatedUser);
}
