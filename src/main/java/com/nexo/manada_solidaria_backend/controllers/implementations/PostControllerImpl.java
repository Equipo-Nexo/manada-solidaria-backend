package com.nexo.manada_solidaria_backend.controllers.implementations;

import com.nexo.manada_solidaria_backend.controllers.interfaces.PostController;
import com.nexo.manada_solidaria_backend.controllers.requests.CreatePostRequest;
import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;
import com.nexo.manada_solidaria_backend.services.interfaces.PostService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PostControllerImpl implements PostController {

    private final PostService postService;

    @Override
    public PostResponse createPost(CreatePostRequest request) {
        return postService.createPost(request);
    }
}
