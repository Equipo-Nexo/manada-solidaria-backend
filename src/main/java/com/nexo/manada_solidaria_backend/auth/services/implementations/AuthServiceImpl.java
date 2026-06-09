package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.nexo.manada_solidaria_backend.auth.controllers.responses.LoginResponse;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.AuthService;
import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.nexo.manada_solidaria_backend.auth.utils.SecurityConstants.ROLES_CLAIM_NAME;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JsonWebTokenService jsonWebTokenService;
    private final UserService userService;

    @Override
    public LoginResponse login(String username) {
        User user = userService.loadUserByUsername(username);
        return new LoginResponse(
                jsonWebTokenService.getAccessToken(getUserId(user), getCustomClaims(user)),
                jsonWebTokenService.getRefreshToken(user.getId().toString(), Collections.emptyMap())
        );
    }

    private static Map<String, Object> getCustomClaims(User user) {
        return Map.of(ROLES_CLAIM_NAME, user.getAuthorities());
    }

    private static String getUserId(User user) {
        return user.getId().toString();
    }
}
