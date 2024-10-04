package school.faang.user_service.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Country findOrCreateCountry(String countryName) {
        List<Country> countries = (List<Country>) countryRepository.findAll();

        Optional<Country> countryOptional = countries.stream()
                .filter(country -> country.getTitle().equalsIgnoreCase(countryName))
                .findFirst();

        return countryOptional.orElseGet(() -> new Country(0, countryName, null));
    }
}