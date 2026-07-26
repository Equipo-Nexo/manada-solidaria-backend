package com.nexo.manada_solidaria_backend.animal_posts.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class MockAnimalPostDataUtils {

    public static final String LOST_VALID = """
            {
              "type": "LOST",
              "name": "Perdí a mi perro",
              "description": "Se escapó en el parque",
              "imageId": "cf-image-123",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "reward": 5000,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "ADULT" },
              "location": { "name": "Parque Centenario", "address": "Av. Patricias", "number": 100, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    public static final String ADOPTION_VALID = """
            {
              "type": "ADOPTION",
              "name": "Busco hogar para gata",
              "description": "Rescatada de la calle",
              "imageId": "cf-image-456",
              "phoneNumber": "1122334455",
              "inTransit": false,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "PUPPY" },
              "location": { "name": "Refugio Norte", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    public static final String ADOPTION_IN_TRANSIT = """
            {
              "type": "ADOPTION",
              "name": "Gata en tránsito",
              "description": "En hogar de tránsito",
              "imageId": "cf-image-555",
              "phoneNumber": "1122334455",
              "inTransit": true,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "PUPPY" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    public static final String LOST_WITHOUT_HAS_OWNER = """
            {
              "type": "LOST",
              "name": "Encontré un perro",
              "description": "Estaba solo en la calle",
              "imageId": "cf-image-999",
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Esquina", "address": "Corrientes", "number": 500, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    public static final String WITHOUT_NAME = """
            {
              "type": "LOST",
              "description": "Publicacion sin nombre",
              "imageId": "cf-image-789",
              "hasOwner": false,
              "animal": { "type": "DOG", "size": "LARGE", "gender": "UNKNOWN", "age": "ADULT" },
              "location": { "name": "Plaza", "address": "Mitre", "number": 1, "latitude": -34.0, "longitude": -58.0 }
            }
            """;

    private static final String WITHOUT_DESCRIPTION = """
            {
              "type": "ADOPTION",
              "name": "Gatito en adopción",
              "imageId": "cf-image-111",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_IMAGE_ID = """
            {
              "type": "ADOPTION",
              "name": "Gatito en adopción",
              "description": "Rescatado de la calle",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" },
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_ANIMAL = """
            {
              "type": "ADOPTION",
              "name": "Post sin animal",
              "description": "Descripción válida",
              "imageId": "cf-image-222",
              "location": { "name": "Refugio", "address": "Calle Falsa", "number": 123, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String WITHOUT_LOCATION = """
            {
              "type": "ADOPTION",
              "name": "Post sin ubicación",
              "description": "Descripción válida",
              "imageId": "cf-image-333",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE" }
            }
            """;

    private static final String WITHOUT_TYPE = """
            {
              "name": "Sin tipo",
              "description": "Descripción válida",
              "imageId": "cf-image-000",
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_AGE = """
            {
              "type": "LOST",
              "name": "Sin edad",
              "description": "Falta la edad",
              "imageId": "cf-image-666",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    public static final String INVALID_AGE = """
            {
              "type": "LOST",
              "name": "Edad invalida",
              "description": "La edad no es un valor valido",
              "imageId": "cf-image-badage",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "OLD" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    public static final String LOST_AGE_UNKNOWN = """
            {
              "type": "LOST",
              "name": "Edad desconocida",
              "description": "No se sabe la edad",
              "imageId": "cf-image-unk",
              "phoneNumber": "1122334455",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "UNKNOWN" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_PHONE = """
            {
              "type": "LOST",
              "name": "Sin teléfono",
              "description": "Falta el teléfono",
              "imageId": "cf-image-777",
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "ADULT" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    // LOST "en la calle" (hasOwner=false): el teléfono NO es obligatorio.
    public static final String LOST_STREET_WITHOUT_PHONE = """
            {
              "type": "LOST",
              "name": "Animal en la calle",
              "description": "Lo vi deambulando, no lo tengo conmigo",
              "imageId": "cf-image-street",
              "hasOwner": false,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "ADULT" },
              "location": { "name": "Esquina", "address": "Corrientes", "number": 500, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String ADOPTION_WITHOUT_PHONE = """
            {
              "type": "ADOPTION",
              "name": "Adopción sin teléfono",
              "description": "Falta el teléfono",
              "imageId": "cf-image-aa1",
              "inTransit": false,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "PUPPY" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String ADOPTION_WITHOUT_IN_TRANSIT = """
            {
              "type": "ADOPTION",
              "name": "Adopción sin inTransit",
              "description": "Falta inTransit",
              "imageId": "cf-image-888",
              "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "PUPPY" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;

    private static final String ADOPTION_WITH_REWARD = """
            {
              "type": "ADOPTION",
              "name": "Adopción con reward",
              "description": "El reward no aplica a adopción",
              "imageId": "cf-image-999a",
              "phoneNumber": "1122334455",
              "inTransit": false,
              "reward": 5000,
              "animal": { "type": "CAT", "size": "SMALL", "gender": "FEMALE", "age": "PUPPY" },
              "location": { "name": "Refugio", "address": "Calle", "number": 1, "latitude": -34.5, "longitude": -58.5 }
            }
            """;
    
    public static final String PUT_VALID = """
            {
              "name": "Titulo actualizado",
              "description": "Descripcion actualizada",
              "imageId": "cf-image-put",
              "phoneNumber": "1199887766",
              "reward": 7500,
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR", "color": "negro" },
              "location": { "name": "Refugio Nuevo", "address": "Nueva direccion 456", "number": 999, "latitude": -34.7, "longitude": -58.7 }
            }
            """;

    // Cada uno es un PUT completo al que le falta UN campo obligatorio -> 400. (reward es opcional, se omite.)
    private static final String PUT_WITHOUT_DESCRIPTION = """
            {
              "name": "t", "imageId": "i", "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_IMAGE_ID = """
            {
              "name": "t", "description": "d", "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_ANIMAL = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": "1122334455",
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_LOCATION = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" }
            }
            """;

    private static final String PUT_ANIMAL_WITHOUT_TYPE = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": "1122334455",
              "animal": { "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_ANIMAL_WITHOUT_AGE = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_LOCATION_WITHOUT_NAME = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": "1122334455",
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static Stream<Arguments> provideCreateCases() {
        return Stream.of(
                Arguments.of("Una request LOST valida crea el post y responde el status code correcto", LOST_VALID, HttpStatus.CREATED, "LOST"),
                Arguments.of("Una request ADOPTION valida crea el post y responde el status code correcto", ADOPTION_VALID, HttpStatus.CREATED, "ADOPTION"),
                Arguments.of("Se envia una request del tipo LOST sin el parametro hasOwner, devuelve BAD_REQUEST", LOST_WITHOUT_HAS_OWNER, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request del tipo ADOPTION sin el parametro inTransit, devuelve BAD_REQUEST", ADOPTION_WITHOUT_IN_TRANSIT, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request del tipo ADOPTION con reward, devuelve BAD_REQUEST", ADOPTION_WITH_REWARD, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request sin type, devuelve BAD_REQUEST", WITHOUT_TYPE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request sin name (opcional), se crea igual", WITHOUT_NAME, HttpStatus.CREATED, "IN_STREET"),
                Arguments.of("Se envia una request sin description, devuelve BAD_REQUEST", WITHOUT_DESCRIPTION, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request sin imageId, devuelve BAD_REQUEST", WITHOUT_IMAGE_ID, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Una request LOST con dueno sin phoneNumber, devuelve BAD_REQUEST", WITHOUT_PHONE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Una request ADOPTION sin phoneNumber, devuelve BAD_REQUEST", ADOPTION_WITHOUT_PHONE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Una request LOST en la calle (hasOwner=false) sin phoneNumber se crea igual y vuelve como IN_STREET", LOST_STREET_WITHOUT_PHONE, HttpStatus.CREATED, "IN_STREET"),
                Arguments.of("Se envia una request sin age, devuelve BAD_REQUEST", WITHOUT_AGE, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request sin animal, devuelve BAD_REQUEST", WITHOUT_ANIMAL, HttpStatus.BAD_REQUEST, null),
                Arguments.of("Se envia una request sin location, devuelve BAD_REQUEST", WITHOUT_LOCATION, HttpStatus.BAD_REQUEST, null)
        );
    }

    private static Stream<Arguments> provideCreateFieldCases() {
        return Stream.of(
                Arguments.of("Sin name se crea igual y queda null", WITHOUT_NAME, HttpStatus.CREATED, "$.name", nullValue()),
                Arguments.of("age=UNKNOWN es valido y se persiste", LOST_AGE_UNKNOWN, HttpStatus.CREATED, "$.animal.age", is("UNKNOWN")),
                Arguments.of("Un age invalido informa el campo", INVALID_AGE, HttpStatus.BAD_REQUEST, "$.errors", hasItem(containsString("age"))),
                Arguments.of("Un age invalido informa los valores permitidos", INVALID_AGE, HttpStatus.BAD_REQUEST, "$.errors", hasItem(containsString("UNKNOWN")))
        );
    }

    private static Stream<Arguments> provideUpdateInvalidCases() {
        return Stream.of(
                Arguments.of("Sin description devuelve BAD_REQUEST", PUT_WITHOUT_DESCRIPTION),
                Arguments.of("Sin imageId devuelve BAD_REQUEST", PUT_WITHOUT_IMAGE_ID),
                Arguments.of("Sin animal devuelve BAD_REQUEST", PUT_WITHOUT_ANIMAL),
                Arguments.of("Sin location devuelve BAD_REQUEST", PUT_WITHOUT_LOCATION),
                Arguments.of("Animal sin type devuelve BAD_REQUEST", PUT_ANIMAL_WITHOUT_TYPE),
                Arguments.of("Animal sin age devuelve BAD_REQUEST", PUT_ANIMAL_WITHOUT_AGE),
                Arguments.of("Location sin name devuelve BAD_REQUEST", PUT_LOCATION_WITHOUT_NAME)
        );
    }

    private static Stream<Arguments> provideInTransitCases() {
        return Stream.of(
                Arguments.of("Una adopcion con inTransit=true (transito ya resuelto) queda en SEARCHING_ADOPT", ADOPTION_IN_TRANSIT, "SEARCHING_ADOPT"),
                Arguments.of("Una adopcion con inTransit=false (sin transito) queda en SEARCHING_ADOPT_AND_TRANSIT", ADOPTION_VALID, "SEARCHING_ADOPT_AND_TRANSIT")
        );
    }

    private static Stream<Arguments> provideLostTransitionCases() {
        return Stream.of(
                Arguments.of("Un LOST con dueno transiciona a SEARCHING", LOST_VALID, "SEARCHING"),
                Arguments.of("Un LOST en la calle transiciona a TO_RESCUE", LOST_STREET_WITHOUT_PHONE, "TO_RESCUE")
        );
    }

    private static Stream<Arguments> provideResponseTypeCases() {
        return Stream.of(
                Arguments.of("Un LOST con dueno vuelve como LOST", LOST_VALID, "LOST"),
                Arguments.of("Un LOST sin dueno vuelve como IN_STREET", LOST_STREET_WITHOUT_PHONE, "IN_STREET"),
                Arguments.of("Una adopcion vuelve como ADOPTION", ADOPTION_VALID, "ADOPTION")
        );
    }

    private static Stream<Arguments> provideFilterCases() {
        return Stream.of(
                Arguments.of("Sin filtros devuelve todos los posts", null, null, 5),
                Arguments.of("type=LOST devuelve solo los perdidos con dueno", "LOST", null, 2),
                Arguments.of("type=IN_STREET devuelve solo los que estan en la calle", "IN_STREET", null, 1),
                Arguments.of("type=ADOPTION devuelve solo las adopciones", "ADOPTION", null, 2),
                Arguments.of("status=CREATED devuelve solo los posts en ese estado", null, "CREATED", 1),
                Arguments.of("status=SEARCHING devuelve los perdidos con dueno en busqueda", null, "SEARCHING", 1),
                Arguments.of("status=TO_RESCUE devuelve los de la calle", null, "TO_RESCUE", 1),
                Arguments.of("status=FOUND no devuelve resultados", null, "FOUND", 0),
                Arguments.of("status=SEARCHING_ADOPT_AND_TRANSIT devuelve las adopciones con transito", null, "SEARCHING_ADOPT_AND_TRANSIT", 1),
                Arguments.of("status=SEARCHING_ADOPT no devuelve resultados", null, "SEARCHING_ADOPT", 0),
                Arguments.of("status=ADOPTED devuelve las adopciones concretadas", null, "ADOPTED", 1),
                Arguments.of("type=ADOPTION y status=ADOPTED devuelve las adopciones adoptadas", "ADOPTION", "ADOPTED", 1),
                Arguments.of("type=LOST y status=ADOPTED no devuelve resultados", "LOST", "ADOPTED", 0),
                Arguments.of("type=LOST y status=SEARCHING excluye a los de la calle", "LOST", "SEARCHING", 1),
                Arguments.of("type=IN_STREET y status=TO_RESCUE devuelve al de la calle", "IN_STREET", "TO_RESCUE", 1)
        );
    }
}
