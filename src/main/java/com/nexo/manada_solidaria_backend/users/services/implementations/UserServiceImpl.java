package com.nexo.manada_solidaria_backend.users.services.implementations;

import com.nexo.manada_solidaria_backend.animal_posts.services.interfaces.AnimalPostService;
import com.nexo.manada_solidaria_backend.auth.controllers.requests.CreateUserRequest;
import com.nexo.manada_solidaria_backend.campaigns.services.interfaces.CampaignService;
import com.nexo.manada_solidaria_backend.common.controllers.requests.PhoneNumberRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateProfileRequest;
import com.nexo.manada_solidaria_backend.users.controllers.requests.UpdateRolesRequest;
import com.nexo.manada_solidaria_backend.users.controllers.responses.*;
import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.Profile;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import com.nexo.manada_solidaria_backend.users.data.repositories.UserRepository;
import com.nexo.manada_solidaria_backend.users.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CampaignService campaignService;
    private final AnimalPostService animalPostService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User with that username was not found"));
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User was not found"));
    }

    @Override
    public void createUser(CreateUserRequest createUserRequest) {
        try {
            userRepository.saveAndFlush(buildUser(createUserRequest));
        } catch (DataIntegrityViolationException e) {
            log.error("Username already exists", e);
            throw new ResponseStatusException(BAD_REQUEST, "El nombre de usuario ya existe");
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Error creando el usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUser(UUID userId, User authenticatedUser) {
        User user = Optional.ofNullable(userId)
                .map(this::getUserById)
                .orElse(authenticatedUser);
        return UserDetailResponse.from(user, getUserPosts(user, null), countCompletedPosts(user));
    }

    @Override
    public List<UserPostResponse> getUserPosts(User user, String type) {
        return (requireAllPosts(type) ? getAllUserPosts(user) : getUserPostsByType(user, type))
                .sorted(Comparator.comparingLong(UserPostResponse::getCreatedSince))
                .toList();
    }

    @Override
    public ProfileResponse updateProfile(UpdateProfileRequest request, User authenticatedUser) {
        authenticatedUser.getProfile().update(request);
        userRepository.save(authenticatedUser);
        return ProfileResponse.from(authenticatedUser.getProfile());
    }

    @Override
    public List<Rol> updateRoles(UpdateRolesRequest request, User authenticatedUser) {
        authenticatedUser.getProfile().updateRoles(request);
        userRepository.save(authenticatedUser);
        return authenticatedUser.getProfile().getRoles();
    }

    private long countCompletedPosts(User user) {
        return animalPostService.countFinishedUserAnimalPosts(user)
                + campaignService.countFinishedUserCampaigns(user);
    }

    private static boolean requireAllPosts(String type) {
        return type == null || type.isBlank();
    }

    private Stream<UserPostResponse> getAllUserPosts(User user) {
        return Stream.of(getUserAnimalPosts(user), getUserCampaigns(user), getUserFundraisingCampaigns(user))
                .flatMap(Function.identity());
    }

    private Stream<UserPostResponse> getUserPostsByType(User user, String type) {
        return switch (type) {
            case "campaign" -> getUserCampaigns(user);
            case "animal" -> getUserAnimalPosts(user);
            case "fundraising" -> getUserFundraisingCampaigns(user);
            default -> throw new ResponseStatusException(BAD_REQUEST, "requested type is not supported");
        };
    }

    private Stream<UserPostResponse> getUserAnimalPosts(User user) {
        return animalPostService.getUserAnimalPosts(user)
                .stream()
                .map(AnimalUserPostResponse::new);
    }

    private Stream<UserPostResponse> getUserFundraisingCampaigns(User user) {
        return campaignService.getUserFundraisingCampaigns(user)
                .stream()
                .map(FundraisingCampaignResponse::new);
    }

    private Stream<UserPostResponse> getUserCampaigns(User user) {
        return campaignService.getUserCampaigns(user)
                .stream()
                .map(CampaignUserPostResponse::new);
    }

    private User buildUser(CreateUserRequest createUserRequest) {
        return new User(
                createUserRequest.getUsername(),
                passwordEncoder.encode(createUserRequest.getPassword()),
                new Profile(
                        createUserRequest.getEmail(),
                        PhoneNumberRequest.toDomain(createUserRequest.getPhoneNumber()),
                        Optional.ofNullable(createUserRequest.getRoles())
                                .orElse(List.of(Rol.COMMUNITY))

                )
        );
    }
}
