package com.nexo.manada_solidaria_backend.animal_posts.utils;

import com.nexo.manada_solidaria_backend.animal_posts.controllers.requests.AnimalPostFilter;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MockAnimalPostDataUtils {

    public static final String LOST_VALID = """
            {
              "type": "LOST",
              "name": "Perdí a mi perro",
              "description": "Se escapó en el parque",
              "imageId": "cf-image-123",
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "UNKNOWN" },
              "location": { "name": "Plaza", "address": "Corrientes", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String WITHOUT_AREA_CODE = """
            {
              "type": "LOST",
              "name": "Sin codigo de area",
              "description": "Falta el codigo de area",
              "imageId": "cf-image-888",
              "phoneNumber": {"number": "436249"},
              "hasOwner": true,
              "animal": { "type": "DOG", "size": "MEDIUM", "gender": "MALE", "age": "ADULT" },
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
              "phoneNumber": {"areaCode": "3511", "number": "998877"},
              "reward": 7500,
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR", "color": "negro" },
              "location": { "name": "Refugio Nuevo", "address": "Nueva direccion 456", "number": 999, "latitude": -34.7, "longitude": -58.7 }
            }
            """;

    public static Stream<Arguments> provideGetAnimalPostCases(){
        return Stream.of(
                Arguments.of(
                        "Una publicación inexistente devuelve 404",
                        UUID.randomUUID().toString(),
                        "VALID",
                        HttpStatus.NOT_FOUND
                ),
                Arguments.of(
                        "Sin token devuelve 401",
                        UUID.randomUUID().toString(),
                        null,
                        HttpStatus.UNAUTHORIZED
                ),
                Arguments.of(
                        "Con token inválido devuelve 401",
                        UUID.randomUUID().toString(),
                        "Bearer " + INVALID_ACCESS_TOKEN,
                        HttpStatus.UNAUTHORIZED
                )
        );
    }

    private static Stream<Arguments> provideOwnerFieldCases() {
        return Stream.of(
                Arguments.of("Trae el usuario de quien publica", "$.owner.username", is("test-user")),
                Arguments.of("Trae su foto de perfil", "$.owner.profileImageURL", is("cf-test-user")),
                Arguments.of("Trae sus roles", "$.owner.roles", contains("COMMUNITY"))
        );
    }

    private static Stream<Arguments> provideOwnerRolesCases() {
        return Stream.of(
                Arguments.of("Devuelve todos los roles, no uno solo", List.of(Rol.RESCUER, Rol.TRANSITIONAL_HOME)),
                Arguments.of("Con un unico rol devuelve ese", List.of(Rol.COMMUNITY))
        );
    }

    public static Stream<Arguments> provideExistingAnimalPosts() {
        return Stream.of(
                Arguments.of(
                        "Obtiene una publicación LOST existente",
                        "55555555-5555-5555-5555-555555555555",
                        "LOST",
                        "Perdí mi perro",
                        notNullValue()
                ),
                Arguments.of(
                        "Obtiene una publicación ADOPTION existente",
                        "99999999-9999-9999-9999-999999999999",
                        "ADOPTION",
                        "Busco hogar para gata",
                        notNullValue()
                ),
                Arguments.of(
                        "Una publicación en la calle traida de la BBDD vuelve con phoneNumber null",
                        "77777777-7777-7777-7777-777777777777",
                        "IN_STREET",
                        "Perro encontrado",
                        nullValue()
                )
        );
    }

    // Cada uno es un PUT completo al que le falta UN campo obligatorio -> 400. (reward es opcional, se omite.)
    private static final String PUT_WITHOUT_DESCRIPTION = """
            {
              "name": "t", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_IMAGE_ID = """
            {
              "name": "t", "description": "d", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_ANIMAL = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_WITHOUT_LOCATION = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" }
            }
            """;

    private static final String PUT_ANIMAL_WITHOUT_TYPE = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "animal": { "size": "LARGE", "gender": "FEMALE", "age": "SENIOR" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_ANIMAL_WITHOUT_AGE = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "animal": { "type": "CAT", "size": "LARGE", "gender": "FEMALE" },
              "location": { "name": "n", "address": "a", "number": 1, "latitude": -34.6, "longitude": -58.4 }
            }
            """;

    private static final String PUT_LOCATION_WITHOUT_NAME = """
            {
              "name": "t", "description": "d", "imageId": "i", "phoneNumber": {"areaCode": "3533", "number": "436249"},
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
                Arguments.of("Una request LOST con dueno sin areaCode, devuelve BAD_REQUEST", WITHOUT_AREA_CODE, HttpStatus.BAD_REQUEST, null),
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
                Arguments.of("Uno de la calle sin telefono devuelve phoneNumber null", LOST_STREET_WITHOUT_PHONE, HttpStatus.CREATED, "$.phoneNumber", nullValue()),
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

    private static Stream<Arguments> provideStatusTransitionCases() {
        return Stream.of(
                Arguments.of("Un perdido en busqueda pasa a encontrado", AnimalPostFilter.LOST, "SEARCHING", "FOUND", HttpStatus.OK),
                Arguments.of("Uno de la calle para rescatar pasa a rescatado", AnimalPostFilter.IN_STREET, "TO_RESCUE", "RESCUED", HttpStatus.OK),
                Arguments.of("Una adopcion con transito pasa a adoptada", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT", "ADOPTED", HttpStatus.OK),
                Arguments.of("Una adopcion sin transito pasa a adoptada", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT_AND_TRANSIT", "ADOPTED", HttpStatus.OK),
                Arguments.of("Una adopcion que consigue transito deja de buscarlo", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT_AND_TRANSIT", "SEARCHING_ADOPT", HttpStatus.OK),
                Arguments.of("Una adopcion que pierde el transito vuelve a buscarlo", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT", "SEARCHING_ADOPT_AND_TRANSIT", HttpStatus.OK),
                Arguments.of("ADOPTED no es un estado valido para un perdido", AnimalPostFilter.LOST, "SEARCHING", "ADOPTED", HttpStatus.BAD_REQUEST),
                Arguments.of("FOUND no es un estado valido para una adopcion", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT", "FOUND", HttpStatus.BAD_REQUEST),
                Arguments.of("Un estado inexistente devuelve BAD_REQUEST", AnimalPostFilter.LOST, "SEARCHING", "NO_EXISTE", HttpStatus.BAD_REQUEST),
                Arguments.of("Un perdido ya encontrado no puede volver a transicionar", AnimalPostFilter.LOST, "FOUND", "FOUND", HttpStatus.CONFLICT),
                Arguments.of("Una adopcion ya adoptada no puede volver a transicionar", AnimalPostFilter.ADOPTION, "ADOPTED", "ADOPTED", HttpStatus.CONFLICT),
                Arguments.of("Uno de la calle NO puede pasar a encontrado", AnimalPostFilter.IN_STREET, "TO_RESCUE", "FOUND", HttpStatus.CONFLICT),
                Arguments.of("Un perdido con dueno NO puede pasar a rescatado", AnimalPostFilter.LOST, "SEARCHING", "RESCUED", HttpStatus.CONFLICT),
                Arguments.of("Uno de la calle ya rescatado no puede volver a transicionar", AnimalPostFilter.IN_STREET, "RESCUED", "RESCUED", HttpStatus.CONFLICT),
                Arguments.of("No se puede transicionar desde CREATED", AnimalPostFilter.LOST, "CREATED", "FOUND", HttpStatus.CONFLICT)
        );
    }

    private static Stream<Arguments> provideTransitionUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
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

    private static Stream<Arguments> provideHappyCaseFieldCases() {
        return Stream.of(
                Arguments.of("El mas reciente primero: la adopcion adoptada ayer", "$.content[0].name", is("Michi adoptada")),
                Arguments.of("Trae la descripcion", "$.content[0].description", is("Encontro familia")),
                Arguments.of("Trae la imagen de la publicacion", "$.content[0].imageUrl", is("cf-img-adopted")),
                Arguments.of("Trae el estado final", "$.content[0].status", is("ADOPTED")),
                Arguments.of("Trae el usuario del dueno", "$.content[0].owner.username", is("vecino")),
                Arguments.of("Trae la foto de perfil del dueno", "$.content[0].owner.profileImageURL", is("cf-perfil-vecino")),
                Arguments.of("Solo devuelve los estados finales", "$.totalElements", is(3)),
                Arguments.of("Trae todos los roles del dueno", "$.content[?(@.name == 'Firulais volvio')].owner.roles[*]", hasItem("RESCUER")),
                Arguments.of("Un dueno de la comunidad trae su rol", "$.content[?(@.name == 'Michi adoptada')].owner.roles[*]", hasItem("COMMUNITY")),
                Arguments.of("Resuelta ayer es reciente", "$.content[?(@.name == 'Michi adoptada')].isRecent", hasItem(true)),
                Arguments.of("Resuelta hace 5 dias es reciente", "$.content[?(@.name == 'Firulais volvio')].isRecent", hasItem(true)),
                Arguments.of("Resuelta hace 7 dias ya no es reciente", "$.content[?(@.name == 'Rex historico')].isRecent", hasItem(false))
        );
    }

    private static Stream<Arguments> provideHappyCaseStatusCases() {
        return Stream.of(
                Arguments.of("FOUND es un caso feliz", AnimalPostFilter.LOST, "FOUND", 1),
                Arguments.of("ADOPTED es un caso feliz", AnimalPostFilter.ADOPTION, "ADOPTED", 1),
                Arguments.of("RESCUED es un caso feliz", AnimalPostFilter.IN_STREET, "RESCUED", 1),
                Arguments.of("CREATED no es un caso feliz", AnimalPostFilter.LOST, "CREATED", 0),
                Arguments.of("SEARCHING no es un caso feliz", AnimalPostFilter.LOST, "SEARCHING", 0),
                Arguments.of("TO_RESCUE no es un caso feliz", AnimalPostFilter.IN_STREET, "TO_RESCUE", 0),
                Arguments.of("SEARCHING_ADOPT no es un caso feliz", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT", 0),
                Arguments.of("SEARCHING_ADOPT_AND_TRANSIT no es un caso feliz", AnimalPostFilter.ADOPTION, "SEARCHING_ADOPT_AND_TRANSIT", 0)
        );
    }

    private static Stream<Arguments> provideHappyCasesUnauthorizedCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
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

    private static Stream<Arguments> provideAnimalFilterCases() {
        return Stream.of(
                Arguments.of("Sin filtros devuelve todas las publicaciones", Map.of(), 3),
                Arguments.of("animalType=DOG devuelve los perros", Map.of("animalType", "DOG"), 2),
                Arguments.of("animalType=CAT devuelve los gatos", Map.of("animalType", "CAT"), 1),
                Arguments.of("animalType=OTHER no devuelve resultados", Map.of("animalType", "OTHER"), 0),
                Arguments.of("animalSize=SMALL devuelve los chicos", Map.of("animalSize", "SMALL"), 1),
                Arguments.of("animalSize=LARGE devuelve los grandes", Map.of("animalSize", "LARGE"), 1),
                Arguments.of("animalGender=FEMALE devuelve las hembras", Map.of("animalGender", "FEMALE"), 2),
                Arguments.of("animalGender=UNKNOWN no devuelve resultados", Map.of("animalGender", "UNKNOWN"), 0),
                Arguments.of("animalAge=PUPPY devuelve los cachorros", Map.of("animalAge", "PUPPY"), 1),
                Arguments.of("animalAge=SENIOR devuelve los mayores", Map.of("animalAge", "SENIOR"), 1),
                Arguments.of("animalColor=BLACK devuelve los negros", Map.of("animalColor", "BLACK"), 2),
                Arguments.of("animalColor=GRAY no devuelve resultados", Map.of("animalColor", "GRAY"), 0),
                Arguments.of("animalType=DOG y animalColor=BLACK devuelve los perros negros",
                        Map.of("animalType", "DOG", "animalColor", "BLACK"), 2),
                Arguments.of("animalType=DOG y animalSize=SMALL devuelve al perro chico",
                        Map.of("animalType", "DOG", "animalSize", "SMALL"), 1),
                Arguments.of("animalType=DOG y animalColor=WHITE no devuelve resultados",
                        Map.of("animalType", "DOG", "animalColor", "WHITE"), 0),
                Arguments.of("type=ADOPTION y animalColor=BLACK cruza el filtro viejo con el nuevo",
                        Map.of("type", "ADOPTION", "animalColor", "BLACK"), 1),
                Arguments.of("type=IN_STREET y animalType=CAT devuelve a la gata de la calle",
                        Map.of("type", "IN_STREET", "animalType", "CAT"), 1),
                Arguments.of("status=SEARCHING y animalAge=PUPPY combina estado con atributo del animal",
                        Map.of("status", "SEARCHING", "animalAge", "PUPPY"), 1)
        );
    }

    private static Stream<Arguments> provideInvalidFilterCases() {
        return Stream.of(
                Arguments.of("type invalido", "type", "INVALIDO"),
                Arguments.of("animalType invalido", "animalType", "DINOSAURIO"),
                Arguments.of("animalSize invalido", "animalSize", "ENORME"),
                Arguments.of("animalGender invalido", "animalGender", "OTRO"),
                Arguments.of("animalAge invalido", "animalAge", "BEBE")
        );
    }

}
