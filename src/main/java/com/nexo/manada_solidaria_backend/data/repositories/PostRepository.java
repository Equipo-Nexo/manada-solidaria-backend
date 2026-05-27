package com.nexo.manada_solidaria_backend.data.repositories;

import com.nexo.manada_solidaria_backend.data.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
