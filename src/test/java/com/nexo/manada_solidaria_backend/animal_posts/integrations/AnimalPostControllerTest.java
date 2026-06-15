package com.nexo.manada_solidaria_backend.animal_posts.integrations;

import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalGender;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalSize;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.AnimalType;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusAdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.enums.StatusLostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.AdoptionPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.Animal;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPost;
import com.nexo.manada_solidaria_backend.animal_posts.data.models.LostPostStatusHistory;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.AdoptionPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.data.repositories.LostPostRepository;
import com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils;
import com.nexo.manada_solidaria_backend.common.integrations.base.BaseAuthenticatedIntegrationTest;
import com.nexo.manada_solidaria_backend.locations.data.models.Location;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nexo.manada_solidaria_backend.common.utils.MockBaseDataUtils.INVALID_ACCESS_TOKEN;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AnimalPostControllerTest extends BaseAuthenticatedIntegrationTest {

    @Autowired
    private LostPostRepository lostPostRepository;
    @Autowired
    private AdoptionPostRepository adoptionPostRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("POST /animal-post — código de estado por payload")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideCreateCases")
    void createTests(
            String testName,
            String body,
            HttpStatus expectedStatus,
            String expectedType
    ) throws Exception {
        var result = mockMvc.perform(
                post("/animal-post")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().is(expectedStatus.value()));

        if (expectedStatus.is2xxSuccessful()) {
            result
                    .andExpect(jsonPath("$.type").value(expectedType))
                    .andExpect(jsonPath("$.status").value("CREATED"));
        }
    }

    @Test
    @DisplayName("POST /animal-post válido: el post, refleja el input y queda con el owner")
    void create_persistsPostWithAuthenticatedOwnerAndInputData() throws Exception {
        UUID adminId = userRepository.findByUsername("admin").orElseThrow().getId();

        mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("LOST"))
                .andExpect(jsonPath("$.title").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.description").value("Se escapó en el parque"))
                .andExpect(jsonPath("$.imageUrl").value("cf-image-123")) // imageId del request → imageUrl
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.animal.type").value("DOG"))
                .andExpect(jsonPath("$.animal.size").value("MEDIUM"))
                .andExpect(jsonPath("$.animal.gender").value("MALE"))
                .andExpect(jsonPath("$.location.name").value("Parque Centenario"))
                .andExpect(jsonPath("$.location.address").value("Av. Patricias"))
                .andExpect(jsonPath("$.location.number").value(100))
                .andExpect(jsonPath("$.ownerId").value(adminId.toString()));

        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Perdí a mi perro"))
                .andExpect(jsonPath("$.content[0].ownerId").value(adminId.toString()))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @Test
    @DisplayName("POST /animal-post LOST sin hasOwner, con el mensaje del validator")
    void create_lostWithoutHasOwner_returnsValidatorMessage() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_WITHOUT_HAS_OWNER)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors",
                        hasItem("El parámetro hasOwner es obligatorio cuando el tipo de publicación es LOST")));
    }

    @Test
    @DisplayName("POST /animal-post sin token devuelve 401")
    void create_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /animal-post con token inválido devuelve 401")
    void create_withInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/animal-post")
                                .header("Authorization", "Bearer " + INVALID_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MockAnimalPostDataUtils.LOST_VALID)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /animal-posts devuelve los casos del mas nuevo al ma viejo")
    void list_returnsNewestFirst() throws Exception {
        saveLostPost("Caso perdido", StatusLostPost.CREATED);
        Thread.sleep(2);
        saveAdoptionPost("Caso adopción", StatusAdoptionPost.CREATED);

        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Caso adopción"))
                .andExpect(jsonPath("$.content[1].title").value("Caso perdido"))
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @DisplayName("GET /animal-posts?type= filtra por tipo y estado")
    @ParameterizedTest(name = "{index} - {0}")
    @MethodSource("com.nexo.manada_solidaria_backend.animal_posts.utils.MockAnimalPostDataUtils#provideFilterCases")
    void filterTests(String testName, String type, int expectedCount) throws Exception {
        saveLostPost("Lost creado", StatusLostPost.CREATED);
        saveLostPost("Lost buscando", StatusLostPost.SEARCHING);
        saveAdoptionPost("Adopción creada", StatusAdoptionPost.CREATED);
        saveAdoptionPost("Adopción adoptada", StatusAdoptionPost.ADOPTED);

        MockHttpServletRequestBuilder request = get("/animal-posts")
                .header("Authorization", "Bearer " + accessToken);
        if (type != null) {
            request = request.param("type", type);
        }

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(expectedCount));
    }

    @Test
    @DisplayName("GET /animal-posts?type=SEARCHING devuelve solo peridos en ese estado")
    void filterByStatus_returnsOnlyMatchingStatusAndSubtype() throws Exception {
        saveLostPost("En búsqueda", StatusLostPost.SEARCHING);
        saveLostPost("Recién creado", StatusLostPost.CREATED);
        saveAdoptionPost("Adopción", StatusAdoptionPost.SEARCHING_ADOPT);

        mockMvc.perform(
                        get("/animal-posts")
                                .param("type", "SEARCHING")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("En búsqueda"))
                .andExpect(jsonPath("$.content[0].type").value("LOST"))
                .andExpect(jsonPath("$.content[0].status").value("SEARCHING"));
    }

    @Test
    @DisplayName("GET /animal-posts pagination")
    void list_paginates() throws Exception {
        saveLostPost("Uno", StatusLostPost.CREATED);
        saveLostPost("Dos", StatusLostPost.CREATED);
        saveAdoptionPost("Tres", StatusAdoptionPost.CREATED);

        mockMvc.perform(
                        get("/animal-posts")
                                .param("page", "0")
                                .param("size", "2")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    @DisplayName("GET /animal-posts sin publicaciones devuelve página vacía")
    void list_whenEmpty_returnsEmptyPage() throws Exception {
        mockMvc.perform(
                        get("/animal-posts").header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /animal-posts?type=INVALIDO devuelve 400")
    void list_withInvalidType_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        get("/animal-posts")
                                .param("type", "INVALIDO")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /animal-posts sin token devuelve 401")
    void list_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/animal-posts"))
                .andExpect(status().isUnauthorized());
    }

    private void saveLostPost(String title, StatusLostPost status) {
        LostPost post = new LostPost(title, "Descripción", "cf-img", null, true, null, location(), animal());
        post.setStatusHistory(new ArrayList<>(List.of(new LostPostStatusHistory(status, post))));
        lostPostRepository.save(post);
    }

    private void saveAdoptionPost(String title, StatusAdoptionPost status) {
        AdoptionPost post = new AdoptionPost(title, "Descripción", "cf-img", null, null, animal(), location());
        post.setStatusHistory(new ArrayList<>(List.of(new AdoptionPostStatusHistory(status, post))));
        adoptionPostRepository.save(post);
    }

    private Animal animal() {
        Animal animal = new Animal();
        animal.setType(AnimalType.DOG);
        animal.setSize(AnimalSize.MEDIUM);
        animal.setGender(AnimalGender.MALE);
        return animal;
    }

    private Location location() {
        return new Location("Parque", "Av. Patricias", 100, -34.6, -58.4);
    }
}
