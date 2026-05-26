package com.nexo.manada_solidaria_backend.services.interfaces;

import com.nexo.manada_solidaria_backend.controllers.requests.CreatePostRequest;
import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;

public interface PostService {
    PostResponse createPost(CreatePostRequest request);
}
