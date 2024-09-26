package school.faang.user_service.service.country;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country findCountryByTitle(String title) {
        return countryRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
    }

    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }
}
