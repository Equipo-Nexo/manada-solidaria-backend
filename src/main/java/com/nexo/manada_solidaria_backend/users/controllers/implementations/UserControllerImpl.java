package com.nexo.manada_solidaria_backend.users.controllers.implementations;

import com.nexo.manada_solidaria_backend.users.controllers.interfaces.UserController;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.ProfileResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserPostResponse;
import com.nexo.manada_solidaria_backend.users.controllers.responses.UserResponse;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public List<UserResponse> getUsers(String username, Rol role) {
        return userService.getUsers(username, role);
    }

    @Override
    public List<UserPostResponse> getUserPosts(User user, String type) {
        return userService.getUserPosts(user, type);
    }

    @Override
    public ProfileResponse updateProfile(UpdateProfileRequest request, User authenticatedUser) {
        return userService.updateProfile(request, authenticatedUser);
    }

    @Override
    public List<Rol> updateRoles(UpdateRolesRequest request, User authenticatedUser) {
        return userService.updateRoles(request, authenticatedUser);
    }
}
