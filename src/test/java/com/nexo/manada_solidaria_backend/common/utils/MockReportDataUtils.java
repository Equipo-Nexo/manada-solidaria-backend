package com.nexo.manada_solidaria_backend.common.utils;

import com.nexo.manada_solidaria_backend.common.controllers.requests.CreateReportPostRequest;
import com.nexo.manada_solidaria_backend.common.data.enums.ReportableType;
import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;

public class MockReportDataUtils {

    public static final UUID MOCK_POST_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final UUID MOCK_DONATION_CAMPAIGN_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    public static final UUID MOCK_FUNDRAISING_CAMPAIGN_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");

    public static final CreateReportPostRequest VALID_ANIMAL_REPORT_REQUEST =
            new CreateReportPostRequest(MOCK_POST_ID, ReportableType.ANIMAL_POST);

    public static final CreateReportPostRequest VALID_DONATION_CAMPAIGN_REPORT_REQUEST =
            new CreateReportPostRequest(MOCK_DONATION_CAMPAIGN_ID, ReportableType.CAMPAIGN);

    public static final CreateReportPostRequest VALID_FUNDRAISING_CAMPAIGN_REPORT_REQUEST =
            new CreateReportPostRequest(MOCK_FUNDRAISING_CAMPAIGN_ID, ReportableType.CAMPAIGN);

    private static Stream<Arguments> provideUnauthorizedReportCases() {
        return Stream.of(
                Arguments.of("Sin token en el reporte", null),
                Arguments.of("Con token inválido en el reporte", INVALID_ACCESS_TOKEN)
        );
    }
}