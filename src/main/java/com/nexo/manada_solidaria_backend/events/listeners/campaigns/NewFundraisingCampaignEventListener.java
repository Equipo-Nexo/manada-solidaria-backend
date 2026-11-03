package com.nexo.manada_solidaria_backend.events.listeners.campaigns;

import com.nexo.manada_solidaria_backend.events.data.models.NewCampaignCreatedEvent;
import com.nexo.manada_solidaria_backend.events.listeners.BaseEventNotificationListener;
import com.nexo.manada_solidaria_backend.notifications.components.recipients.data.NotificationContext;
import com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType;
import com.nexo.manada_solidaria_backend.notifications.services.interfaces.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

import static com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType.FUNDRAISING;
import static com.nexo.manada_solidaria_backend.notifications.models.enums.NotificationType.NEW_FUNDRAISING_CAMPAIGN;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.FUNDRAISING_ID;
import static com.nexo.manada_solidaria_backend.notifications.utils.NotificationParamUtil.USER;

@Component
@Slf4j
public class NewFundraisingCampaignEventListener extends BaseEventNotificationListener<NewCampaignCreatedEvent> {

    public NewFundraisingCampaignEventListener(NotificationService notificationService) {
        super(notificationService);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(NewCampaignCreatedEvent event) {
        processEvent(event);
    }

    @Override
    protected boolean shouldNotify(NewCampaignCreatedEvent event) {
        return event.type() == FUNDRAISING;
    }

    @Override
    protected NotificationType getNotificationType() {
        return NEW_FUNDRAISING_CAMPAIGN;
    }

    @Override
    protected Map<String, Object> getParams(NewCampaignCreatedEvent event) {
        return Map.of(
                USER, event.ownerUsername(),
                FUNDRAISING_ID, event.campaignId()
        );
    }

    @Override
    protected NotificationContext getContext(NewCampaignCreatedEvent event) {
        return new NotificationContext(event.ownerId());
    }
}
