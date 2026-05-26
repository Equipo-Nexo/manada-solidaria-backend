package com.nexo.manada_solidaria_backend.services.implementations;

import com.nexo.manada_solidaria_backend.controllers.requests.CreatePostRequest;
import com.nexo.manada_solidaria_backend.controllers.responses.PostResponse;
import com.nexo.manada_solidaria_backend.data.models.Post;
import com.nexo.manada_solidaria_backend.data.repositories.PostRepository;
import com.nexo.manada_solidaria_backend.services.interfaces.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public PostResponse createPost(CreatePostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());

        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getDescription()
        );
    }
}
