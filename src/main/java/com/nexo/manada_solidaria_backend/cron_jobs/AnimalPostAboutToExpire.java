package com.nexo.manada_solidaria_backend.cron_jobs;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class AnimalPostAboutToExpire {

    private final AnimalPostService animalPostService;
    private final NotificationService notificationService;

    @Scheduled(cron = "* * * * * *")
    void sendAboutToExpirePostNotification() {
        getAnimalPostsAboutToExpire()
                .forEach(animalPost -> {
                    notificationService.notify();
                });
    }

    private Stream<AnimalPost<?, ?>> getAnimalPostsAboutToExpire() {
        return animalPostService.getAboutToExpirePosts().stream();
    }

}
