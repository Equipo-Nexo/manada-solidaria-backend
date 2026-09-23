package com.nexo.manada_solidaria_backend.animal_posts.components;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@animalPostAuthorization.isOwner(#animalPostId, principal)")
public @interface AnimalPostOwner {
}
