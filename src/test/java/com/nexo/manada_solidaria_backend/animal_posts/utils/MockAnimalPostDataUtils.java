package com.nexo.manada_solidaria_backend.animal_posts.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public class MockAnimalPostDataUtils {

    public static final String LOST_VALID = """
            {
              "type": "LOST",
              "title": "Perdí a mi perro",
              "description": "Se escapó en el parque",
              "imageId": "cf-image-123",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "reward": 5000,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "2 años" },
              "location": { "name": "Parque Centenario", "address": "Av. Patricias", "number": 100, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    public static final String ADOPTION_VALID = """
            {
              "type": "ADOPTION",
              "title": "Busco hogar para gata",
              "description": "Rescatada de la calle",
              "imageId": "cf-image-456",
              "phoneNumber": "1122334455",
              "inTransit": false,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "1 año" },
              "location": { "name": "Refugio Norte", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    public static final String ADOPTION_IN_TRANSIT = """
            {
              "type": "ADOPTION",
              "title": "Gata en tránsito",
              "description": "En hogar de tránsito",
              "imageId": "cf-image-555",
              "phoneNumber": "1122334455",
              "inTransit": true,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "1 año" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    public static final String LOST_WITHOUT_HAS_OWNER = """
            {
              "type": "LOST",
              "title": "Encontré un perro",
              "description": "Estaba solo en la calle",
              "imageId": "cf-image-999",
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Esquina", "address": "Corrientes", "number": 500, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_TITLE = """
            {
              "type": "ADOPTION",
              "description": "Sin título",
              "imageId": "cf-image-789",
              "animal": { "type": "DOG", "size": "LARGE", "gender": "UNKNOWN" },
              "location": { "name": "Plaza", "address": "Mitre", "number": 1, "latitude": -34.0, "longitude": -58.0 }
            }
            """;

    private static final String WITHOUT_DESCRIPTION = """
            {
              "type": "ADOPTION",
              "title": "Gatito en adopción",
              "imageId": "cf-image-111",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_IMAGE_ID = """
            {
              "type": "ADOPTION",
              "title": "Gatito en adopción",
              "description": "Rescatado de la calle",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_ANIMAL = """
            {
              "type": "ADOPTION",
              "title": "Post sin animal",
              "description": "Descripción válida",
              "imageId": "cf-image-222",
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_LOCATION = """
            {
              "type": "ADOPTION",
              "title": "Post sin ubicación",
              "description": "Descripción válida",
              "imageId": "cf-image-333",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" }
            }
            """;

    private static final String WITHOUT_TYPE = """
            {
              "title": "Sin tipo",
              "description": "Descripción válida",
              "imageId": "cf-image-000",
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_AGE = """
            {
              "type": "LOST",
              "title": "Sin edad",
              "description": "Falta la edad",
              "imageId": "cf-image-666",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_PHONE = """
            {
              "type": "LOST",
              "title": "Sin teléfono",
              "description": "Falta el teléfono",
              "imageId": "cf-image-777",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "2 años" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String ADOPTION_WITHOUT_IN_TRANSIT = """
            {
              "type": "ADOPTION",
              "title": "Adopción sin inTransit",
              "description": "Falta inTransit",
              "imageId": "cf-image-888",
              "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "1 año" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String ADOPTION_WITH_REWARD = """
            {
              "type": "ADOPTION",
              "title": "Adopción con reward",
              "description": "El reward no aplica a adopción",
              "imageId": "cf-image-999a",
              "phoneNumber": "1122334455",
              "inTransit": false,
              "reward": 5000,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "1 año" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static Stream<Arguments> provideCreateCases() {
        return Stream.of(
                Arguments.of("LOST válido devuelve 201", LOST_VALID, HttpStatus.CREATED, "LOST"),
                Arguments.of("ADOPTION válido devuelve 201", ADOPTION_VALID, HttpStatus.CREATED, "ADOPTION"),
                Arguments.of("LOST sin hasOwner devuelve 400", LOST_WITHOUT_HAS_OWNER, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin title devuelve 400", WITHOUT_TITLE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin description devuelve 400", WITHOUT_DESCRIPTION, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin imageId devuelve 400", WITHOUT_IMAGE_ID, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin animal devuelve 400", WITHOUT_ANIMAL, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin location devuelve 400", WITHOUT_LOCATION, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin type devuelve 400", WITHOUT_TYPE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin age devuelve 400", WITHOUT_AGE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("sin phoneNumber devuelve 400", WITHOUT_PHONE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("ADOPTION sin inTransit devuelve 400", ADOPTION_WITHOUT_IN_TRANSIT, HttpStatus.BAD_REQUEST, null),
                Arguments.of("ADOPTION con reward devuelve 400", ADOPTION_WITH_REWARD, HttpStatus.BAD_REQUEST, null)
        );
    }

    // El dataset sembrado en filterTests: 2 lost (CREATED, SEARCHING) + 2 adoption (CREATED, ADOPTED).
    private static Stream<Arguments> provideFilterCases() {
        return Stream.of(
                Arguments.of("sin filtro trae todos", null, 4),
                Arguments.of("LOST trae los perdidos", "LOST", 2),
                Arguments.of("ADOPTION trae las adopciones", "ADOPTION", 2),
                Arguments.of("SEARCHING trae perdidos en búsqueda", "SEARCHING", 1),
                Arguments.of("FOUND sin resultados", "FOUND", 0),
                Arguments.of("CREATED trae solo adopciones", "CREATED", 1),
                Arguments.of("ADOPTED trae adoptados", "ADOPTED", 1)
        );
    }
}
