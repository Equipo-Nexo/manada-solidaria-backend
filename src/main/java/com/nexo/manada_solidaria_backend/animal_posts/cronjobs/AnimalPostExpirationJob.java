package com.nexo.manada_solidaria_backend.animal_posts.cronjobs;

import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnimalPostExpirationJob {

    private final AnimalPostService animalPostService;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireInactivePosts() {
        log.info("Starting scheduled job: expireInactivePosts");
        animalPostService.expireInactivePosts();
        log.info("Completed scheduled job: expireInactivePosts");
    }
}
