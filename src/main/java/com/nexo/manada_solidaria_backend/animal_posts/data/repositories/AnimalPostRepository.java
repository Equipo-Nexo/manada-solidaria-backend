package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnimalPostRepository extends JpaRepository<AnimalPost, UUID> {
}
