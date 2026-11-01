package com.nexo.manada_solidaria_backend.common.data.repositories;

import com.nexo.manada_solidaria_backend.common.data.models.ReportPost;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost, UUID> {

    boolean existsByPostIdAndOwner(UUID postId, User owner);

    long countByPostId(UUID postId);

    void deleteByPostId(UUID postId);
}