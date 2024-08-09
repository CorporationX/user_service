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

    public synchronized Country getCountryOrCreate(String title) {
        Optional<Country> country = countryRepository.findByTitle(title);
        if (country.isEmpty()) {
            Country newCountry = new Country();
            newCountry.setTitle(title);
            return countryRepository.save(newCountry);
        } else {
            return country.get();
        }
    }
}