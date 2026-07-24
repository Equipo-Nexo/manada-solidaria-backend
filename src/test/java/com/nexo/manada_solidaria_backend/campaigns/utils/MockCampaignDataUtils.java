package com.nexo.manada_solidaria_backend.campaigns.utils;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.UpdateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.CampaignStatus;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.DonationCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.enums.NewsCampaignCategory;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationCampaign;
import com.nexo.manada_solidaria_backend.campaigns.data.models.DonationItem;
import com.nexo.manada_solidaria_backend.campaigns.data.models.FundraisingCampaign;
import com.nexo.manada_solidaria_backend.locations.controllers.requests.UpdateLocationRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class MockCampaignDataUtils {

    private static final LocationRequest UBICACION_VILLA_MARIA = new LocationRequest(
            "Villa María", "Belgrano", 450, -32.41, -63.24
    );

    private static final LocationRequest UBICACION_CORDOBA = new LocationRequest(
            "Córdoba Capital", "San Martín", 1020, -32.42, -63.25
    );

    private static final LocationRequest UBICACION_SOLO_OBLIGATORIOS = new LocationRequest(
            "Plaza Principal", null, null, -32.43, -63.26
    );

    private static final LocalDateTime NEWS_START_DATE =
            LocalDateTime.of(2026, 8, 1, 10, 0);

    private static final LocalDateTime NEWS_END_DATE =
            LocalDateTime.of(2026, 8, 10, 18, 0);


    public static final CreateCampaignRequest NEWS_VALID = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Campaña de Vacunación",
            "Estaremos vacunando gratis este sábado.",
            "cf-image-news-123",
            "154154154",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null,
            null,
            NEWS_START_DATE,
            NEWS_END_DATE,
            NewsCampaignCategory.VACCINATION
    );


    public static final CreateCampaignRequest NEWS_UBICACION_OPCIONAL = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Campaña Limpieza",
            "Limpieza de espacios verdes.",
            null,
            "1542345678",
            UBICACION_SOLO_OBLIGATORIOS,
            null,
            null,
            null,
            null,
            NEWS_START_DATE,
            NEWS_END_DATE,
            NewsCampaignCategory.OTHER
    );


    public static final CreateCampaignRequest FUNDRAISING_VALID_FULL = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Operación de mofli",
            "Necesitamos juntar fondos para cirugía",
            "cf-image-fundraising-456",
            "123456789",
            UBICACION_CORDOBA,
            "recaudacion.mofli",
            150000L,
            LocalDate.now().plusYears(1),
            null,
            null,
            null,
            null
    );


    public static final CreateCampaignRequest FUNDRAISING_VALID_OPEN = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Fondo de emergencia",
            "Recaudación abierta para balanceado.",
            null,
            "1234567891",
            UBICACION_VILLA_MARIA,
            "ayudemos.patitas",
            null,
            null,
            null,
            null,
            null,
            null
    );


    public static final CreateCampaignRequest DONATION_VALID = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Ayuda para refugio",
            "Necesitamos donaciones para los animales.",
            "cf-image-donation-123",
            "12345678912",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null,
            List.of(
                    new CreateCampaignRequest.DonationItemRequest(
                            "Alimento balanceado",
                            DonationCampaignCategory.FOOD
                    ),
                    new CreateCampaignRequest.DonationItemRequest(
                            "Mantas",
                            DonationCampaignCategory.CLOTHING_AND_BLANKETS
                    )
            ),
            null,
            null,
            null
    );


    private static final CreateCampaignRequest NEWS_WITH_ACCOUNT_ALIAS = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia con Alias",
            "Intenta meter alias en una noticia.",
            null,
            "1234567812",
            UBICACION_VILLA_MARIA,
            "alias.no.valido",
            null,
            null,
            null,
            NEWS_START_DATE,
            NEWS_END_DATE,
            NewsCampaignCategory.VACCINATION
    );


    private static final CreateCampaignRequest FUNDRAISING_WITHOUT_ALIAS = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Recaudación sin alias",
            "Se olvidaron de configurar dónde transferir.",
            null,
            "154154154",
            UBICACION_VILLA_MARIA,
            null,
            10000L,
            null,
            null,
            null,
            null,
            null
    );


    private static final CreateCampaignRequest NEWS_WITH_FUNDRAISING_FIELDS = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia inválida",
            "Tiene campos de recaudación.",
            null,
            "154154154",
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            5000L,
            null,
            null,
            NEWS_START_DATE,
            NEWS_END_DATE,
            NewsCampaignCategory.CASTRATION
    );


    private static final CreateCampaignRequest DONATION_WITHOUT_ITEMS = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Donación inválida",
            "No tiene elementos.",
            null,
            "1541541234",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null,
            null,
            null,
            null,
            null
    );


    private static final CreateCampaignRequest WITHOUT_TITLE = new CreateCampaignRequest(
            CampaignType.NEWS,
            null,
            "Falta el título",
            null,
            "123456789",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null,
            null,
            NEWS_START_DATE,
            NEWS_END_DATE,
            NewsCampaignCategory.OTHER
    );


    private static final CreateCampaignRequest FUNDRAISING_NEGATIVE_AMOUNT = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Monto Negativo",
            "No debería pasar.",
            null,
            "123456789",
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            -500L,
            null,
            null,
            null,
            null,
            null
    );


    private static final CreateCampaignRequest FUNDRAISING_PAST_DATE = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Fecha Pasada",
            "No debería pasar.",
            null,
            "123456789",
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            null,
            LocalDate.now().minusDays(5),
            null,
            null,
            null,
            null
    );

    private static final CreateCampaignRequest NEWS_END_DATE_BEFORE_START_DATE = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia con fechas inválidas",
            "La fecha de fin es anterior a la de inicio.",
            null,
            "123456789",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null,
            null,
            NEWS_START_DATE,
            NEWS_START_DATE.minusDays(1),
            NewsCampaignCategory.VACCINATION
    );

    private static Stream<Arguments> provideCreateCases() {
        return Stream.of(
                Arguments.of(
                        "La campaña NEWS se crea correctamente",
                        NEWS_VALID,
                        HttpStatus.CREATED,
                        "vaccination"
                ),
                Arguments.of(
                        "La campaña NEWS con ubicación opcional se crea correctamente",
                        NEWS_UBICACION_OPCIONAL,
                        HttpStatus.CREATED,
                        "other"
                ),
                Arguments.of(
                        "La campaña FUNDRAISING completa se crea correctamente",
                        FUNDRAISING_VALID_FULL,
                        HttpStatus.CREATED,
                        "fundraising"
                ),
                Arguments.of(
                        "La campaña FUNDRAISING abierta se crea correctamente",
                        FUNDRAISING_VALID_OPEN,
                        HttpStatus.CREATED,
                        "fundraising"
                ),
                Arguments.of(
                        "La campaña DONATION con items se crea correctamente",
                        DONATION_VALID,
                        HttpStatus.CREATED,
                        "donation"
                ),
                Arguments.of(
                        "La campaña NEWS con alias falla",
                        NEWS_WITH_ACCOUNT_ALIAS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña FUNDRAISING sin alias falla",
                        FUNDRAISING_WITHOUT_ALIAS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña NEWS con campos de fundraising falla",
                        NEWS_WITH_FUNDRAISING_FIELDS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña DONATION sin items falla",
                        DONATION_WITHOUT_ITEMS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña sin título falla",
                        WITHOUT_TITLE,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña FUNDRAISING con monto negativo falla",
                        FUNDRAISING_NEGATIVE_AMOUNT,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña FUNDRAISING con fecha pasada falla",
                        FUNDRAISING_PAST_DATE,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña NEWS con fecha de fin anterior a la fecha de inicio falla",
                        NEWS_END_DATE_BEFORE_START_DATE,
                        HttpStatus.BAD_REQUEST,
                        null
                )
        );
    }


    public static com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign buildNewsModel(
            com.nexo.manada_solidaria_backend.users.data.models.User owner
    ) {
        com.nexo.manada_solidaria_backend.locations.data.models.Location location =
                new com.nexo.manada_solidaria_backend.locations.data.models.Location();

        location.setName("Villa María");
        location.setLatitude(-32.41);
        location.setLongitude(-63.24);

        return new com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign(
                "Título Noticia Test",
                "Descripción Noticia",
                "img-1",
                "url-1",
                "123456789",
                location,
                owner,
                NEWS_START_DATE,
                NEWS_END_DATE,
                NewsCampaignCategory.VACCINATION
        );
    }


    public static FundraisingCampaign buildFundraisingModel(
            com.nexo.manada_solidaria_backend.users.data.models.User owner
    ) {
        com.nexo.manada_solidaria_backend.locations.data.models.Location location =
                new com.nexo.manada_solidaria_backend.locations.data.models.Location();

        location.setName("Córdoba");
        location.setLatitude(-32.42);
        location.setLongitude(-63.25);

        return new FundraisingCampaign(
                "Título Recaudación de Dinero Test",
                "Descripción Recaudación de Dinero",
                "img-2",
                "url-2",
                "123456789",
                location,
                owner,
                "alias.recaudacion",
                50000L,
                LocalDate.now().plusDays(10)
        );
    }

    public static DonationCampaign buildDonationModel(
            com.nexo.manada_solidaria_backend.users.data.models.User owner
    ) {
        com.nexo.manada_solidaria_backend.locations.data.models.Location location =
                new com.nexo.manada_solidaria_backend.locations.data.models.Location();

        location.setName("Villa María");
        location.setLatitude(-32.41);
        location.setLongitude(-63.24);

        DonationCampaign campaign = new DonationCampaign(
                "Título Donación Test",
                "Descripción Donación",
                "img-3",
                "url-3",
                "123456789",
                location,
                owner,
                LocalDate.now().plusDays(10)
        );

        campaign.addItem(new DonationItem(
                "Bolsa de alimento",
                DonationCampaignCategory.FOOD
        ));

        return campaign;
    }

    public static UpdateCampaignRequest buildDonationUpdateRequest() {

        return new UpdateCampaignRequest(
                "Título Donación Editado",
                "Descripción editada",
                "img-updated",
                "999999999",
                new UpdateLocationRequest(
                        "Villa María",
                        "Nueva dirección",
                        100,
                        -32.40,
                        -63.20
                ),
                null,
                null,
                null,
                LocalDate.now().plusMonths(2),
                null,
                null,
                null
        );
    }


    public static UpdateCampaignRequest buildFundraisingUpdateRequest() {

        return new UpdateCampaignRequest(
                "Recaudación Editada",
                "Nueva descripción",
                "img-fundraising-updated",
                "111111111",
                new UpdateLocationRequest(
                        "Córdoba",
                        "Nueva dirección",
                        200,
                        -31.41,
                        -64.18
                ),
                "nuevo.alias",
                100000L,
                25000L,
                LocalDate.now().plusMonths(3),
                null,
                null,
                null
        );
    }


    public static UpdateCampaignRequest buildNewsUpdateRequest() {

        return new UpdateCampaignRequest(
                "Noticia Editada",
                "Nueva descripción noticia",
                "img-news-updated",
                "222222222",
                new UpdateLocationRequest(
                        "Villa María",
                        "Plaza nueva",
                        50,
                        -32.41,
                        -63.24
                ),
                null,
                null,
                null,
                null,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 5, 18, 0),
                NewsCampaignCategory.OTHER
        );
    }

    private static Stream<Arguments> provideFinalDonationStatuses() {
        return Stream.of(
                Arguments.of("Una donacion en estado FINISHED no se puede eliminar", CampaignStatus.FINISHED),
                Arguments.of("Una donacion en estado COMPLETED tampoco se puede eliminar", CampaignStatus.COMPLETED)
        );
    }
}