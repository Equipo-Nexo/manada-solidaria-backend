package com.nexo.manada_solidaria_backend.campaigns.utils;

import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CampaignType;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest;
import com.nexo.manada_solidaria_backend.campaigns.controllers.requests.CreateCampaignRequest.LocationRequest;
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
            null
    );

    public static final CreateCampaignRequest NEWS_UBICACION_OPCIONAL = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Campaña Limpieza",
            "Limpieza de espacios verdes en el barrio.",
            null,
            UBICACION_SOLO_OBLIGATORIOS,
            null,
            null
    );

    public static final CreateCampaignRequest DONATION_VALID_FULL = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Operación de mofli",
            "Necesitamos juntar fondos para la cirugía de mofli.",
            "cf-image-donation-456",
            UBICACION_CORDOBA,
            150000L,
            LocalDate.now().plusYears(1)
    );

    public static final CreateCampaignRequest DONATION_VALID_OPEN = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Fondo de emergencia",
            "Donaciones abiertas para comprar balanceado.",
            null,
            UBICACION_VILLA_MARIA,
            null,
            null
    );

    private static final CreateCampaignRequest NEWS_WITH_DONATION_FIELDS = new CreateCampaignRequest(
            CampaignType.NEWS,
            "Noticia Inválida",
            "Tiene campos de donación",
            null,
            UBICACION_VILLA_MARIA,
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
            null
    );

    private static final CreateCampaignRequest DONATION_NEGATIVE_AMOUNT = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Monto Negativo",
            "No debería pasar",
            null,
            UBICACION_VILLA_MARIA,
            -500L,
            null
    );

    private static final CreateCampaignRequest DONATION_PAST_DATE = new CreateCampaignRequest(
            CampaignType.DONATION,
            "Fecha Pasada",
            "No debería pasar",
            null,
            UBICACION_VILLA_MARIA,
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
                        "La campaña de tipo DONATION completa se crea exitosamente y responde el HTTP status correspondiente",
                        DONATION_VALID_FULL,
                        HttpStatus.CREATED,
                        "DONATION"
                ),
                Arguments.of(
                        "La campaña de tipo DONATION abierta se crea exitosamente y responde el HTTP status correspondiente",
                        DONATION_VALID_OPEN,
                        HttpStatus.CREATED,
                        "DONATION"
                ),
                Arguments.of(
                        "La campaña de tipo NEWS con campos de recaudación falla en la validación y devuelve un HTTP status erróneo",
                        NEWS_WITH_DONATION_FIELDS,
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
                        "La campaña de tipo DONATION con monto negativo falla en la validación y devuelve un HTTP status erróneo",
                        DONATION_NEGATIVE_AMOUNT,
                        HttpStatus.BAD_REQUEST,
                        null
                ),
                Arguments.of(
                        "La campaña de tipo DONATION con fecha de fin en el pasado falla en la validación y devuelve un HTTP status erróneo",
                        DONATION_PAST_DATE,
                        HttpStatus.BAD_REQUEST,
                        null
                )
        );
    }
}