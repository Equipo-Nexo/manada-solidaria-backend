package com.nexo.manada_solidaria_backend.terms_acceptances.services.interfaces;

import com.nexo.manada_solidaria_backend.users.data.models.User;

public interface TermsAcceptanceService {

    void accept(User user, String termsVersion);
}
