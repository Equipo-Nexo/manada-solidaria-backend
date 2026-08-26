package com.nexo.manada_solidaria_backend.campaigns.integrations;

import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationFundraisingCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.models.Campaign;
import com.nexo.manada_solidaria_backend.campaigns.data.repositories.CampaignRepository;
import com.nexo.manada_solidaria_backend.campaigns.services.implementations.CampaignServiceImpl;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SqlMergeMode(SqlMergeMode.MergeMode.OVERRIDE)
@Sql(
        scripts = {
                "/sql/data-setup.sql",
                "/sql/campaigns/campaign-cronjob-data.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class CampaignServiceIntegrationTest extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private CampaignServiceImpl campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private UserRepository userRepository;

    private User admin;

    @BeforeEach
    void setUp() {
        admin = userRepository.findByUsername("admin").orElseThrow();
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource(
            "com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideExpiredDonationAndFundraisingCampaigns"
    )
    @DisplayName("Las campañas DONATION y FUNDRAISING vencidas se finalizan")
    void expiredDonationAndFundraisingCampaignsAreFinalized(
            String description,
            UUID campaignId
    ) {
        campaignService.finalizeExpiredDonationAndFundraisingCampaigns();

        Campaign<?, ?> campaignAfter =
                campaignRepository.findById(campaignId).orElseThrow();

        assertEquals(
                DonationFundraisingCampaignStatus.FINISHED,
                campaignAfter.getCurrentStatus().getStatus()
        );

        assertTrue(campaignAfter.isFinished());
        assertNotNull(campaignAfter.getFinishedAt());
    }


    @Test
    @DisplayName("Una campaña NEWS vencida pasa de STARTED a FINISHED")
    void expiredNewsCampaignIsFinalized() {

        UUID campaignId =
                UUID.fromString("40000000-0000-0000-0000-000000000013");

        campaignService.finalizeExpiredNewsCampaigns();

        Campaign<?, ?> campaignAfter =
                campaignRepository.findById(campaignId).orElseThrow();

        assertEquals(
                NewsCampaignStatus.FINISHED,
                campaignAfter.getCurrentStatus().getStatus()
        );

        assertTrue(campaignAfter.isFinished());
        assertNotNull(campaignAfter.getFinishedAt());
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource(
            "com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideNonExpiredDonationAndFundraisingCampaigns"
    )
    @DisplayName("Las campañas DONATION y FUNDRAISING no vencidas no se finalizan")
    void nonExpiredDonationAndFundraisingCampaignsAreNotFinalized(
            String description,
            UUID campaignId,
            DonationFundraisingCampaignStatus expectedStatus
    ) {
        campaignService.finalizeExpiredDonationAndFundraisingCampaigns();

        Campaign<?, ?> campaignAfter =
                campaignRepository.findById(campaignId).orElseThrow();

        assertEquals(
                expectedStatus,
                campaignAfter.getCurrentStatus().getStatus()
        );

        assertFalse(campaignAfter.isFinished());
        assertNull(campaignAfter.getFinishedAt());
    }


    @Test
    @DisplayName("Una NEWS no vencida no se finaliza")
    void nonExpiredNewsCampaignIsNotFinalized() {

        UUID campaignId =
                UUID.fromString("40000000-0000-0000-0000-000000000002");

        campaignService.finalizeExpiredNewsCampaigns();

        Campaign<?, ?> campaignAfter =
                campaignRepository.findById(campaignId).orElseThrow();

        assertEquals(
                NewsCampaignStatus.CREATED,
                campaignAfter.getCurrentStatus().getStatus()
        );

        assertFalse(campaignAfter.isFinished());
        assertNull(campaignAfter.getFinishedAt());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(
            "com.nexo.manada_solidaria_backend.campaigns.utils.MockCampaignDataUtils#provideCompletedDonationAndFundraisingCampaigns"
    )
    @DisplayName("Las campañas DONATION y FUNDRAISING COMPLETED no se finalizan por cronjob")
    void completedDonationAndFundraisingCampaignsAreNotFinalized(
            String description,
            UUID campaignId
    ) {
        campaignService.finalizeExpiredDonationAndFundraisingCampaigns();

        Campaign<?, ?> campaignAfter =
                campaignRepository.findById(campaignId).orElseThrow();

        assertEquals(
                DonationFundraisingCampaignStatus.COMPLETED,
                campaignAfter.getCurrentStatus().getStatus()
        );

        assertTrue(campaignAfter.isFinished());
        assertNull(campaignAfter.getFinishedAt());
    }
}