package com.nexo.manada_solidaria_backend.animal_posts.data.repositories;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Reaction;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    boolean existsByPostAndOwner(AnimalPost<?, ?> post, User owner);

    @Query("SELECT r.post.id FROM Reaction r WHERE r.owner = :owner AND r.post.id IN :postIds")
    Set<UUID> findPostIdsReactedBy(@Param("owner") User owner, @Param("postIds") Collection<UUID> postIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM Reaction r WHERE r.post = :post AND r.owner = :owner")
    void deleteByPostAndOwner(@Param("post") AnimalPost<?, ?> post, @Param("owner") User owner);
}
