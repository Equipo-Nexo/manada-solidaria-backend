package com.nexo.manada_solidaria_backend.controllers.interfaces;

import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/posts")
public interface PostController {
    @GetMapping
    List<PostResponse> getAllPosts();

    @GetMapping("/{id}")
    PostResponse getPostById(@PathVariable Long id);
}
