package school.faang.user_service.service.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Optional<Country> findCountryByTitle(String title) {
        return countryRepository.findByTitle(title);
    }

    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    public Country findCountryAndSaveIfNotExists(String title) {
        return findCountryByTitle(title)
                .orElse(saveCountry(Country.builder().build()));
    }
}
