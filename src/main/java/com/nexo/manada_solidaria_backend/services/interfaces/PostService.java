package com.nexo.manada_solidaria_backend.services.interfaces;

import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getAllPosts();

    PostResponse getPostById(Long id);
}
