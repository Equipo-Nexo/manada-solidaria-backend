package com.nexo.manada_solidaria_backend.campaigns.utils;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest.LocationRequest;
import com.nexo.manada_solidaria_backend.campaigns.data.models.FundraisingCampaign;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
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

    public static final CreateCampaignRequest NEWS_VALID = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Campaña de Vacunación",
            "Estaremos vacunando gratis este sábado en la plaza principal.",
            "cf-image-news-123",
            UBICACION_VILLA_MARIA,
            null,
            null,
            null
    );

    public static final CreateCampaignRequest NEWS_UBICACION_OPCIONAL = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Campaña Limpieza",
            "Limpieza de espacios verdes en el barrio.",
            null,
            UBICACION_SOLO_OBLIGATORIOS,
            null,
            null,
            null
    );

    public static final CreateCampaignRequest FUNDRAISING_VALID_FULL = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Operación de mofli",
            "Necesitamos juntar fondos para la cirugía de mofli.",
            "cf-image-fundraising-456",
            UBICACION_CORDOBA,
            "recaudacion.mofli",
            150000L,
            LocalDate.now().plusYears(1)
    );

    public static final CreateCampaignRequest FUNDRAISING_VALID_OPEN = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Fondo de emergencia",
            "Recaudacion abierta para comprar balanceado.",
            null,
            UBICACION_VILLA_MARIA,
            "ayudemos.patitas",
            null,
            null
    );

    private static final CreateCampaignRequest NEWS_WITH_ACCOUNT_ALIAS = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia con Alias",
            "Intenta meter un alias de banco en una noticia",
            null,
            UBICACION_VILLA_MARIA,
            "alias.no.valido",
            null,
            null
    );

    private static final CreateCampaignRequest FUNDRAISING_WITHOUT_ALIAS = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Recaudación sin alias",
            "Se olvidaron de configurar dónde transferir",
            null,
            UBICACION_VILLA_MARIA,
            null,
            10000L,
            null
    );

    private static final CreateCampaignRequest NEWS_WITH_FUNDRAISING_FIELDS = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia Inválida",
            "Tiene campos de donación",
            null,
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            5000L,
            null
    );

    private static final CreateCampaignRequest WITHOUT_TITLE = new CreateCampaignRequest(
            CampaignType.NEWS,
            null,
            "Falta el título",
            null,
            UBICACION_VILLA_MARIA,
            null,
            null,
            null
    );

    private static final CreateCampaignRequest FUNDRAISING_NEGATIVE_AMOUNT = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Monto Negativo",
            "No debería pasar",
            null,
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            -500L,
            null
    );

    private static final CreateCampaignRequest FUNDRAISING_PAST_DATE = new CreateCampaignRequest(
            CampaignType.FUNDRAISING,
            "Fecha Pasada",
            "No debería pasar",
            null,
            UBICACION_VILLA_MARIA,
            "ayudemos.entre.todos",
            null,
            LocalDate.now().minusDays(5)
    );

    private static Stream<Arguments> provideCreateCases() {
        return Stream.of(
                Arguments.of(
                        "La campaña de tipo NEWS se crea exitosamente y responde el HTTP status correspondiente",
                        NEWS_VALID,
                        HttpStatus.CREATED,
                        "NEWS"
                ),
                Arguments.of(
                        "La campaña de tipo NEWS con ubicación opcional (sin calle/número) se crea exitosamente",
                        NEWS_UBICACION_OPCIONAL,
                        HttpStatus.CREATED,
                        "NEWS"
                ),
                Arguments.of(
                        "La campaña de tipo FUNDRAISING completa se crea exitosamente y responde el HTTP status correspondiente",
                        FUNDRAISING_VALID_FULL,
                        HttpStatus.CREATED,
                        "FUNDRAISING"
                ),
                Arguments.of(
                        "La campaña de tipo FUNDRAISING abierta se crea exitosamente y responde el HTTP status correspondiente",
                        FUNDRAISING_VALID_OPEN,
                        HttpStatus.CREATED,
                        "FUNDRAISING"
                ),
                Arguments.of(
                        "La campaña de tipo NEWS con alias de cuenta falla en la validación",
                        NEWS_WITH_ACCOUNT_ALIAS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña de tipo FUNDRAISING sin alias obligatorio falla en la validación",
                        FUNDRAISING_WITHOUT_ALIAS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña de tipo NEWS con campos de recaudación falla en la validación y devuelve un HTTP status erróneo",
                        NEWS_WITH_FUNDRAISING_FIELDS,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña sin título obligatorio falla en la validación y devuelve un HTTP status erróneo",
                        WITHOUT_TITLE,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña de tipo FUNDRAISING con monto negativo falla en la validación y devuelve un HTTP status erróneo",
                        FUNDRAISING_NEGATIVE_AMOUNT,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña de tipo FUNDRAISING con fecha de fin en el pasado falla en la validación y devuelve un HTTP status erróneo",
                        FUNDRAISING_PAST_DATE,
                        HttpStatus.BAD_REQUEST,
                        null
                )
        );
    }

    public static com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign buildNewsModel(com.nexo.manada_solidaria_backend.users.data.models.User owner) {
        com.nexo.manada_solidaria_backend.locations.data.models.Location location =
                new com.nexo.manada_solidaria_backend.locations.data.models.Location();
        location.setName("Villa María");
        location.setLatitude(-32.41);
        location.setLongitude(-63.24);

        return new com.nexo.manada_solidaria_backend.campaigns.data.models.NewsCampaign(
                "Título Noticia Test", "Descripción Noticia", "img-1", "url-1", location, owner
        );
    }

    public static FundraisingCampaign buildFundraisingModel(com.nexo.manada_solidaria_backend.users.data.models.User owner) {
        com.nexo.manada_solidaria_backend.locations.data.models.Location location =
                new com.nexo.manada_solidaria_backend.locations.data.models.Location();
        location.setName("Córdoba");
        location.setLatitude(-32.42);
        location.setLongitude(-63.25);

        return new FundraisingCampaign(
                "Título Recaudación de Dinero Test", "Descripción Recaudación de Dinero", "img-2", "url-2", location, owner,"alias Recaudacion de Dinero", 50000L, LocalDate.now().plusDays(10)
        );
    }
}