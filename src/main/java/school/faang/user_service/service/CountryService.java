package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.concurrent.CountedCompleter;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public Country getSavedCountry (Country country) {
        return countryRepository.findByTitle(country.getTitle())
                .orElseGet(() -> countryRepository.save(country));
    }
}
