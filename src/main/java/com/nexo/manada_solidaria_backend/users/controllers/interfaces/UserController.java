package com.nexo.manada_solidaria_backend.users.controllers.interfaces;

import com.nexo.manada_solidaria_backend.users.controllers.responses.UserPostResponse;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/users")
public interface UserController {

    @GetMapping("/posts")
    List<UserPostResponse> getUserPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String type
    );
}
