package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country getCountryById(Long id) {
        return countryRepository.findById(id).orElseThrow(() -> {
                    log.error("Country with id {} not found", id);
                    return new EntityNotFoundException("Country not found");
                }
        );
    }

    public Optional<Country> findCountryById(Long id) {
        return countryRepository.findById(id);
    }
}
