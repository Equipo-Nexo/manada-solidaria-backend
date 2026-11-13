package com.nexo.manada_solidaria_backend.cron_jobs;

import com.nexo.manada_solidaria_backend.animal_posts.data.models.AnimalPost;
import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import com.nexo.manada_solidaria_backend.password_recovery.data.models.PasswordRecovery;
import com.nexo.manada_solidaria_backend.password_recovery.services.interfaces.PasswordRecoveryService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Slf4j
@AllArgsConstructor
public class AnimalPostAboutToExpire {

    private final AnimalPostService animalPostService;
    private final NotificationService notificationService;
    private final PasswordRecoveryService passwordRecoveryService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            "d 'de' MMMM",
            Locale.forLanguageTag("es-AR")
    );


    @Scheduled(cron = "0 0 20 * * *")
    void sendAboutToExpirePostNotification() {
        log.info("Ejecutando cronjob para enviar notificaciones de publicaciones a punto de expirar.");
        getAnimalPostsAboutToExpire()
                .forEach(animalPost -> {
                    User user = animalPost.getOwner();
                    PasswordRecovery passwordRecovery = passwordRecoveryService.createTotp(user);

                    notificationService.notify(
                            NotificationType.POST_ABOUT_TO_EXPIRE,
                            Map.of(
                                    "animalName", animalPost.getName(),
                                    "expirationDate", formatter.format(LocalDateTime.now().plusDays(10)),
                                    "animalPostId", animalPost.getId(),
                                    "otp", passwordRecovery.getVerificationCode()
                            ),
                            user
                    );
                });
    }

    private Stream<AnimalPost<?, ?>> getAnimalPostsAboutToExpire() {
        return animalPostService.getAboutToExpirePosts().stream();
    }

}
