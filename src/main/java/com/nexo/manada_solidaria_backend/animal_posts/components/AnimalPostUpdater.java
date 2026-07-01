package com.nexo.manada_solidaria_backend.animal_posts.components;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.UpdateAnimalPostRequest;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import org.springframework.stereotype.Component;


@Component
public class AnimalPostUpdater {

    public void applyUpdate(AnimalPost post, UpdateAnimalPostRequest request) {
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setImageUrl(request.imageId());
        post.setPhoneNumber(request.phoneNumber());
        if (post instanceof LostPost lost) lost.setReward(request.reward());
        applyAnimal(post.getAnimal(), request.animal());
        applyLocation(post.getLocation(), request.location());
    }

    private void applyAnimal(Animal animal, UpdateAnimalPostRequest.AnimalUpdate request) {
        animal.setType(request.type());
        animal.setSize(request.size());
        animal.setGender(request.gender());
        animal.setAge(request.age());
        animal.setColor(request.color());
        animal.setBreed(request.breed());
        animal.setFur(request.fur());
        animal.setDescription(request.description());
    }

    private void applyLocation(Location location, UpdateAnimalPostRequest.LocationUpdate request) {
        location.setName(request.name());
        location.setAddress(request.address());
        location.setNumber(request.number());
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
    }
}
