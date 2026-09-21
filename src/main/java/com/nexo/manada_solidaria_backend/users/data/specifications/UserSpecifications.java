package com.nexo.manada_solidaria_backend.users.data.specifications;

import com.nexo.manada_solidaria_backend.users.data.enums.Rol;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {

    }

    /**
     * Builds a specification that filters users by the given role.
     *
     * @param role the role that users must have
     * @return a specification that matches users with the given role
     */
    public static Specification<User> hasRole(Rol role) {
        return (root, query, cb) -> {
            var profile = root.join("profile");
            var roles = profile.join("roles");

            return cb.equal(roles, role);
        };
    }

    /**
     * Builds a specification that returns all registered users.
     *
     * @return a specification that get all users.
     */
    public static Specification<User> all() {
        return (root, query, cb) -> cb.conjunction();
    }
}
