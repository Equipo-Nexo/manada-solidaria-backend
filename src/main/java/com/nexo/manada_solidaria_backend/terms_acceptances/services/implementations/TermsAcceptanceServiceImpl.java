package com.nexo.manada_solidaria_backend.terms_acceptances.services.implementations;

import com.nexo.manada_solidaria_backend.terms_acceptances.data.models.TermsAcceptance;
import com.nexo.manada_solidaria_backend.terms_acceptances.data.repositories.TermsAcceptanceRepository;
import com.nexo.manada_solidaria_backend.terms_acceptances.services.interfaces.TermsAcceptanceService;
import com.nexo.manada_solidaria_backend.users.data.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TermsAcceptanceServiceImpl implements TermsAcceptanceService {

    private final TermsAcceptanceRepository termsAcceptanceRepository;

    @Override
    public void accept(User user, String termsVersion) {
        termsAcceptanceRepository.save(new TermsAcceptance(user, termsVersion));
    }
}
