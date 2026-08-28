package com.nexo.manada_solidaria_backend.users.utils;

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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
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
                Arguments.of("Devuelve las publicaciones del usuario", "$.posts.length()", is(4)),
                Arguments.of("Las publicaciones traen titulo", "$.posts[*].title",
                        hasItem(containsString("de Vacunaci"))),
                Arguments.of("Las publicaciones traen descripcion", "$.posts[*].description",
                        hasItem(containsString("gratuita para perros y gatos"))),
                Arguments.of("Las publicaciones traen estado", "$.posts[*].status", hasItem("CREATED"))
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
                Arguments.of("Devuelve los dias desde el registro", "$.daysSinceRegistration", is(10))
        );
    }

    private static Stream<Arguments> provideUnauthorizedTokenCases() {
        return Stream.of(
                Arguments.of("Sin token", null),
                Arguments.of("Con token invalido", INVALID_ACCESS_TOKEN)
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
