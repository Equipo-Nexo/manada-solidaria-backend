package com.nexo.manada_solidaria_backend.animal_posts.components;

import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AnimalPostRepository;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class AnimalPostAuthorization {

    private final AnimalPostRepository animalPostRepository;

    public boolean isOwner(UUID animalPostId, User user) {
        return animalPostRepository.findById(animalPostId)
                .map(post -> post.getOwner().getId().equals(user.getId()))
                .orElse(true);
    }
}
