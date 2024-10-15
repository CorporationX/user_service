package school.faang.user_service.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.CountryService;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country findOrCreateCountry(String countryName) {
        List<Country> countries = (List<Country>) countryRepository.findAll();

        Optional<Country> countryOptional = countries.stream()
                .filter(country -> country.getTitle().equalsIgnoreCase(countryName))
                .findFirst();

        if (countryOptional.isPresent()) {
            return countryOptional.get();
        } else {
            Country newCountry = new Country(0, countryName, null);
            return countryRepository.save(newCountry);
        }
    }
}