package com.nexo.manada_solidaria_backend.services.implementations;

import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;
import com.nexo.manada_solidaria_backend.data.models.Post;
import com.nexo.manada_solidaria_backend.data.repositories.PostRepository;
import com.nexo.manada_solidaria_backend.services.interfaces.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return toResponse(post);
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
