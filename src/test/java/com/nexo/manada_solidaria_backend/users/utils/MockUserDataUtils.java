package com.nexo.manada_solidaria_backend.users.utils;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.users.controllers.requests.EditableRol;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
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
                Arguments.of("Devuelve el telefono del perfil", "$.profile.phoneNumber", is("1133334444")),
                Arguments.of("Devuelve la foto del perfil", "$.profile.profileImageURL", is("cf-profile-1")),
                Arguments.of("Devuelve los roles", "$.roles", hasItem("COMMUNITY")),
                Arguments.of("Devuelve las publicaciones del usuario", "$.posts.length()", is(4)),
                Arguments.of("Las publicaciones traen titulo", "$.posts[*].title",
                        hasItem(containsString("de Vacunaci"))),
                Arguments.of("Las publicaciones traen descripcion", "$.posts[*].description",
                        hasItem(containsString("gratuita para perros y gatos"))),
                Arguments.of("Las publicaciones traen estado", "$.posts[*].status", hasItem("CREATED")),
                Arguments.of("Metrica: cantidad de publicaciones", "$.metrics.totalPosts", is(4)),
                Arguments.of("Metrica: publicaciones completadas", "$.metrics.completedPosts", is(0)),
                Arguments.of("Metrica: dias desde el registro", "$.metrics.daysSinceRegistration", is(10))
        );
    }

    private static Stream<Arguments> provideCompletedPostsCases() {
        return Stream.of(
                Arguments.of("Una publicacion encontrada cuenta como completada", StatusLostPost.FOUND, 1),
                Arguments.of("Una publicacion en busqueda no cuenta como completada", StatusLostPost.SEARCHING, 0),
                Arguments.of("Una publicacion rescatada cuenta como completada", StatusLostPost.RESCUED, 1),
                Arguments.of("Una publicacion a rescatar no cuenta como completada", StatusLostPost.TO_RESCUE, 0)
        );
    }

    private static Stream<Arguments> provideUnauthorizedTokenCases() {
        return Stream.of(
                Arguments.of("Sin token en el detalle por id", "/users?userId=" + UUID.randomUUID(), null),
                Arguments.of("Con token invalido en el detalle por id", "/users?userId=" + UUID.randomUUID(), INVALID_ACCESS_TOKEN),
                Arguments.of("Sin token en el detalle propio", "/users", null),
                Arguments.of("Con token invalido en el detalle propio", "/users", INVALID_ACCESS_TOKEN)
        );
    }

    private static Stream<Arguments> provideUserResolutionCases() {
        return Stream.of(
                Arguments.of("Sin userId devuelve el usuario del JWT", false, "admin"),
                Arguments.of("Con userId devuelve ese usuario", true, "otro")
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
}
