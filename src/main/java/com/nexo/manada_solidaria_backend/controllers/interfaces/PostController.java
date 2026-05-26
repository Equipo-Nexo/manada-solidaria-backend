package com.nexo.manada_solidaria_backend.controllers.interfaces;

import com.nexo.manada_solidaria_backend.controllers.requests.CreatePostRequest;
import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/posts")
public interface PostController {
    @PostMapping
    PostResponse createPost(@RequestBody CreatePostRequest request);
}
