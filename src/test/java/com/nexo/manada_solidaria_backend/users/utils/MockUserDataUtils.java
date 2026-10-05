package com.nexo.manada_solidaria_backend.users.utils;

import com.nexo.manada_solidaria_backend.users.controllers.requests.EditableRol;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.CreateUserLocationRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class MockUserDataUtils {

    public static final String UPDATE_PROFILE_VALID = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": {"areaCode": "3533", "number": "436249"},
              "profileImageURL": "cf-profile-1"
            }
            """;

    private static final String UPDATE_PROFILE_WITHOUT_IMAGE = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": {"areaCode": "3533", "number": "436249"}
            }
            """;

    private static final String UPDATE_PROFILE_WITHOUT_EMAIL = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "phoneNumber": {"areaCode": "3533", "number": "436249"}
            }
            """;

    private static final String UPDATE_PROFILE_INVALID_EMAIL = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "no-es-un-email",
              "phoneNumber": {"areaCode": "3533", "number": "436249"}
            }
            """;

    private static final String UPDATE_PROFILE_INVALID_PHONE = """
            {
              "name": "Elian",
              "lastname": "Enria",
              "email": "nuevo@mail.com",
              "phoneNumber": {"areaCode": "3533", "number": "telefono-invalido"}
            }
            """;

    public static final UpdateRolesRequest ROLES_WITH_RESCUER =
            new UpdateRolesRequest(List.of(EditableRol.RESCUER, EditableRol.TRANSITIONAL_HOME));

    private static final UpdateRolesRequest ROLES_WITHOUT_RESCUER =
            new UpdateRolesRequest(List.of(EditableRol.TRANSITIONAL_HOME));

    private static final UpdateRolesRequest ROLES_EMPTY =
            new UpdateRolesRequest(List.of());

    private static final String ROLES_WITH_VET = """
            { "roles": ["VET"] }
            """;

    private static final String ROLES_WITH_COMMUNITY = """
            { "roles": ["COMMUNITY"] }
            """;

    public static final String ROLES_MISSING_KEY = """
            { }
            """;

    private static Stream<Arguments> provideUserDetailFieldCases() {
        return Stream.of(
                Arguments.of("Devuelve el username", "$.username", is("admin")),
                Arguments.of("Devuelve el nombre del perfil", "$.profile.name", is("Elian")),
                Arguments.of("Devuelve el apellido del perfil", "$.profile.lastname", is("Enria")),
                Arguments.of("Devuelve el correo del perfil", "$.profile.email", is("admin@mail.com")),
                Arguments.of("Devuelve el codigo de area del perfil", "$.profile.phoneNumber.areaCode", is("3533")),
                Arguments.of("Devuelve el numero de telefono del perfil", "$.profile.phoneNumber.number", is("436249")),
                Arguments.of("Devuelve la foto del perfil", "$.profile.profileImageURL", is("cf-profile-1")),
                Arguments.of("Devuelve los roles", "$.roles", hasItem("COMMUNITY")),
                Arguments.of("Devuelve la fecha de registro", "$.createdAt", is("2025-03-14T10:00:00")),
                Arguments.of("Devuelve las publicaciones del usuario", "$.posts.length()", is(4)),
                Arguments.of("Las publicaciones traen titulo", "$.posts[*].title",
                        hasItem(containsString("de Vacunaci"))),
                Arguments.of("Las publicaciones traen descripcion", "$.posts[*].description",
                        hasItem(containsString("gratuita para perros y gatos"))),
                Arguments.of("Las publicaciones traen estado", "$.posts[*].status", hasItem("CREATED")),
                Arguments.of("Las publicaciones vienen de la mas nueva a la mas vieja", "$.posts[*].postType",
                        contains("animal", "fundraising", "campaign", "campaign")),
                Arguments.of("El animal trae su tipo", "$.posts[0].type", is("IN_STREET")),
                Arguments.of("El animal trae su ubicacion", "$.posts[0].location.address", containsString("Libertador")),
                Arguments.of("El animal trae su telefono", "$.posts[0].phoneNumber.number", is("000000")),
                Arguments.of("El animal trae sus datos", "$.posts[0].animal.type", is("DOG")),
                Arguments.of("El animal trae su nombre en title", "$.posts[0].title", containsString("Perro perdido")),
                Arguments.of("El animal trae su imagen en imageId", "$.posts[0].imageId", nullValue()),
                Arguments.of("El animal trae los dias desde que se publico", "$.posts[0].createdSince", is(0)),
                Arguments.of("La card conserva su fecha de alta", "$.posts[0].createdAt", notNullValue()),
                Arguments.of("La campaña trae su ubicacion", "$.posts[3].location.address", containsString("Sabattini")),
                Arguments.of("La colecta trae su alias", "$.posts[1].accountAlias", is("MANADA.SOLIDARIA")),
                Arguments.of("Conserva los campos que ya usa Mis publicaciones", "$.posts[0].keys()",
                        hasItems("id", "title", "description", "createdSince", "imageId", "postType", "status")),
                Arguments.of("Agrega los datos de la card de la home", "$.posts[0].keys()",
                        hasItems("type", "location", "phoneNumber", "ownerId", "createdAt", "animal", "owner", "reward")),
                Arguments.of("No repite el titulo ni la imagen con otro nombre", "$.posts[0].keys()",
                        allOf(not(hasItem("name")), not(hasItem("imageUrl"))))
        );
    }

    private static Stream<Arguments> provideHomeCardCases() {
        return Stream.of(
                Arguments.of("Animal: trae todo lo de GET /animal-posts", "/animal-posts", "77777777-7777-7777-7777-777777777777", "animal"),
                Arguments.of("Campaña: trae todo lo de GET /campaigns", "/campaigns", "44444444-4444-4444-4444-444444444445", "campaign"),
                Arguments.of("Colecta: trae todo lo de GET /campaigns/fundraising_campaigns", "/campaigns/fundraising_campaigns",
                        "44444444-4444-4444-4444-444444444446", "fundraising")
        );
    }

    private static Stream<Arguments> provideGetUserPostsCardCases() {
        return Stream.of(
                Arguments.of("Sin filtro: de la mas nueva a la mas vieja", null, "$[*].postType",
                        contains("animal", "fundraising", "campaign", "campaign")),
                Arguments.of("Animales: trae su tipo", "animal", "$[0].type", is("IN_STREET")),
                Arguments.of("Animales: trae su ubicacion", "animal", "$[0].location.address", containsString("Libertador")),
                Arguments.of("Animales: trae su telefono", "animal", "$[0].phoneNumber.number", is("000000")),
                Arguments.of("Campañas: solo campañas", "campaign", "$[*].postType", everyItem(is("campaign"))),
                Arguments.of("Campañas: traen su ubicacion", "campaign", "$[0].location.address", notNullValue()),
                Arguments.of("Campañas: de la mas nueva a la mas vieja", "campaign", "$[*].id",
                        contains("44444444-4444-4444-4444-444444444445", "44444444-4444-4444-4444-444444444444")),
                Arguments.of("Colectas: traen su alias", "fundraising", "$[0].accountAlias", is("MANADA.SOLIDARIA"))
        );
    }

    private static Stream<Arguments> provideGetUsersFilterCases() {
        return Stream.of(
                Arguments.of("Sin filtros devuelve todos los usuarios", null, null,
                        List.of("admin", "NOTADMIN", "rescatista", "refugio")),
                Arguments.of("Filtra por nombre de usuario", "NOTADMIN", null,
                        List.of("NOTADMIN")),
                Arguments.of("Un usuario con varios roles aparece al filtrar por cualquiera de ellos", null, "RESCUER",
                        List.of("rescatista", "refugio")),
                Arguments.of("Tambien matchea por un rol secundario", null, "TRANSITIONAL_HOME",
                        List.of("refugio")),
                Arguments.of("Filtra por rol COMMUNITY", null, "COMMUNITY",
                        List.of("admin", "NOTADMIN"))
        );
    }

    private static Stream<Arguments> provideUserFieldCases() {
        return Stream.of(
                Arguments.of("Devuelve el id, que es lo que permite abrir el detalle", "$[0].id", notNullValue()),
                Arguments.of("Devuelve el username del User", "$[0].username", is("rescatista")),
                Arguments.of("Devuelve los roles del Profile", "$[0].roles", contains("RESCUER")),
                Arguments.of("Devuelve el codigo de area", "$[0].phoneNumber.areaCode", is("3533")),
                Arguments.of("Devuelve el numero de telefono", "$[0].phoneNumber.number", is("436249")),
                Arguments.of("Devuelve la foto de perfil", "$[0].profileImageURL", is("cf-rescatista"))
        );
    }

    private static Stream<Arguments> provideUnauthorizedPathCases() {
        return Stream.of(
                Arguments.of("Sin token en el detalle", "/users/" + UUID.randomUUID(), null),
                Arguments.of("Con token invalido en el detalle", "/users/" + UUID.randomUUID(), INVALID_ACCESS_TOKEN),
                Arguments.of("Sin token en el perfil", "/users/" + UUID.randomUUID() + "/profile", null),
                Arguments.of("Con token invalido en el perfil", "/users/" + UUID.randomUUID() + "/profile", INVALID_ACCESS_TOKEN)
        );
    }

    private static Stream<Arguments> provideNotFoundCases() {
        return Stream.of(
                Arguments.of("El detalle de un usuario inexistente", "/users/%s"),
                Arguments.of("El perfil de un usuario inexistente", "/users/%s/profile")
        );
    }

    private static Stream<Arguments> provideUserDetailTypeCases() {
        return Stream.of(
                Arguments.of("Sin type trae todas las publicaciones", false, null, "$.posts[*].postType",
                        contains("animal", "fundraising", "campaign", "campaign")),
                Arguments.of("type=animal trae solo animales", false, "animal", "$.posts[*].postType", contains("animal")),
                Arguments.of("type=campaign trae solo campañas", false, "campaign", "$.posts[*].postType",
                        contains("campaign", "campaign")),
                Arguments.of("type=fundraising trae solo colectas", false, "fundraising", "$.posts[*].postType",
                        contains("fundraising")),
                Arguments.of("Con filtro el perfil viene igual", false, "animal", "$.username", is("admin")),
                Arguments.of("Filtra las publicaciones del usuario del path, no las del token", true, "animal", "$.posts",
                        hasSize(0))
        );
    }

    private static Stream<Arguments> provideUnsupportedTypeCases() {
        return Stream.of(
                Arguments.of("Detalle de usuario con un tipo inexistente", "/users/%s", "perro"),
                Arguments.of("Detalle de usuario con el tipo en mayusculas", "/users/%s", "ANIMAL"),
                Arguments.of("Mis publicaciones con un tipo inexistente", "/users/posts", "perro"),
                Arguments.of("Mis publicaciones con el tipo en mayusculas", "/users/posts", "ANIMAL")
        );
    }

    private static Stream<Arguments> provideUserResolutionCases() {
        return Stream.of(
                Arguments.of("Pedir el usuario autenticado devuelve ese usuario", false, "admin"),
                Arguments.of("Pedir otro usuario devuelve ese otro, no el del token", true, "otro")
        );
    }

    private static Stream<Arguments> provideUserProfileCases() {
        return Stream.of(
                Arguments.of("Devuelve el username", "$.username", is("admin")),
                Arguments.of("Devuelve el nombre", "$.profile.name", is("Elian")),
                Arguments.of("Devuelve el apellido", "$.profile.lastname", is("Enria")),
                Arguments.of("Devuelve el email", "$.profile.email", is("admin@mail.com")),
                Arguments.of("Devuelve el codigo de area", "$.profile.phoneNumber.areaCode", is("3533")),
                Arguments.of("Devuelve el numero de telefono", "$.profile.phoneNumber.number", is("436249")),
                Arguments.of("Devuelve la foto de perfil", "$.profile.profileImageURL", is("cf-profile-1")),
                Arguments.of("Devuelve los roles", "$.roles", hasItem("COMMUNITY")),
                Arguments.of("Devuelve la fecha de registro", "$.createdAt", is("2025-03-14T10:00:00"))
        );
    }

    private static Stream<Arguments> provideUnauthorizedTokenCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
        );
    }

    private static Stream<Arguments> provideCreateLocationValidCases() {
        return Stream.of(
                Arguments.of("Guarda una ubicacion de Buenos Aires", new CreateUserLocationRequest(-34.6037, -58.3816)),
                Arguments.of("Acepta los maximos (90, 180)", new CreateUserLocationRequest(90.0, 180.0)),
                Arguments.of("Acepta los minimos (-90, -180)", new CreateUserLocationRequest(-90.0, -180.0)),
                Arguments.of("Acepta el cero (0, 0)", new CreateUserLocationRequest(0.0, 0.0))
        );
    }

    private static Stream<Arguments> provideCreateLocationInvalidCases() {
        return Stream.of(
                Arguments.of("Sin latitud", new CreateUserLocationRequest(null, -58.3816), "La latitud es obligatoria"),
                Arguments.of("Sin longitud", new CreateUserLocationRequest(-34.6037, null), "La longitud es obligatoria"),
                Arguments.of("Latitud mayor a 90", new CreateUserLocationRequest(90.0001, 0.0), "La latitud debe estar entre -90 y 90"),
                Arguments.of("Latitud menor a -90", new CreateUserLocationRequest(-90.0001, 0.0), "La latitud debe estar entre -90 y 90"),
                Arguments.of("Longitud mayor a 180", new CreateUserLocationRequest(0.0, 180.0001), "La longitud debe estar entre -180 y 180"),
                Arguments.of("Longitud menor a -180", new CreateUserLocationRequest(0.0, -180.0001), "La longitud debe estar entre -180 y 180")
        );
    }

    private static Stream<Arguments> provideGetUserPostsTestCases() {
        return Stream.of(
                Arguments.of("Get all user posts", null, 4),
                Arguments.of("Get all user animal posts", "animal", 1),
                Arguments.of("Get all user campaign posts", "campaign", 2),
                Arguments.of("Get all user fundraising posts", "fundraising", 1)
        );
    }

    private static Stream<Arguments> provideUpdateProfileInvalidCases() {
        return Stream.of(
                Arguments.of("Sin email devuelve BAD_REQUEST", UPDATE_PROFILE_WITHOUT_EMAIL),
                Arguments.of("Email con formato invalido devuelve BAD_REQUEST", UPDATE_PROFILE_INVALID_EMAIL),
                Arguments.of("Telefono con formato invalido devuelve BAD_REQUEST", UPDATE_PROFILE_INVALID_PHONE)
        );
    }

    private static Stream<Arguments> provideUpdateProfileResponseCases() {
        return Stream.of(
                Arguments.of("Devuelve el name enviado", UPDATE_PROFILE_VALID, "$.name", is("Elian")),
                Arguments.of("Devuelve el lastname enviado", UPDATE_PROFILE_VALID, "$.lastname", is("Enria")),
                Arguments.of("Devuelve el email enviado", UPDATE_PROFILE_VALID, "$.email", is("nuevo@mail.com")),
                Arguments.of("Devuelve el areaCode enviado", UPDATE_PROFILE_VALID, "$.phoneNumber.areaCode", is("3533")),
                Arguments.of("Devuelve el phoneNumber enviado", UPDATE_PROFILE_VALID, "$.phoneNumber.number", is("436249")),
                Arguments.of("Devuelve el profileImageURL enviado", UPDATE_PROFILE_VALID, "$.profileImageURL", is("cf-profile-1")),
                Arguments.of("Reemplazo total: el campo omitido queda null", UPDATE_PROFILE_WITHOUT_IMAGE, "$.profileImageURL", nullValue())
        );
    }

    private static Stream<Arguments> provideUpdateRolesCases() {
        return Stream.of(
                Arguments.of("Con RESCUER no se agrega COMMUNITY", ROLES_WITH_RESCUER,
                        List.of(Rol.RESCUER, Rol.TRANSITIONAL_HOME)),
                Arguments.of("Sin RESCUER se agrega COMMUNITY", ROLES_WITHOUT_RESCUER,
                        List.of(Rol.TRANSITIONAL_HOME, Rol.COMMUNITY)),
                Arguments.of("Con la lista vacia queda solo COMMUNITY", ROLES_EMPTY,
                        List.of(Rol.COMMUNITY))
        );
    }

    private static Stream<Arguments> provideNonEditableRoleCases() {
        return Stream.of(
                Arguments.of("VET no es auto-asignable", ROLES_WITH_VET),
                Arguments.of("COMMUNITY no es auto-asignable, lo deriva el back", ROLES_WITH_COMMUNITY)
        );
    }

    private static final double[] VILLA_MARIA = {-32.41, -63.24};
    private static final double[] CORDOBA = {-31.42, -64.19};

    private static Stream<Arguments> provideUsualLocationCases() {
        return Stream.of(
                Arguments.of("Si viajo una vez, gana la ciudad donde mas se conecta",
                        concat(times(3, VILLA_MARIA), times(1, CORDOBA)), VILLA_MARIA[0], VILLA_MARIA[1]),
                Arguments.of("Solo cuentan las ultimas 50: si se mudo, gana la ciudad nueva",
                        concat(times(40, VILLA_MARIA), times(30, CORDOBA)), CORDOBA[0], CORDOBA[1]),
                Arguments.of("Con empate gana la zona mas reciente",
                        concat(times(1, VILLA_MARIA), times(1, CORDOBA)), CORDOBA[0], CORDOBA[1]),
                Arguments.of("Dentro de la misma ciudad devuelve el promedio de sus puntos",
                        List.of(new double[]{-32.40, -63.24}, new double[]{-32.42, -63.22}), -32.41, -63.23)
        );
    }

    private static List<double[]> times(int count, double[] point) {
        return Collections.nCopies(count, point);
    }

    private static List<double[]> concat(List<double[]> older, List<double[]> newer) {
        List<double[]> points = new ArrayList<>(older);
        points.addAll(newer);
        return points;
    }
}
