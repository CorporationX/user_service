package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    CountryRepository countryRepository;

//    public Country createCountry(Country country) {
//        return countryRepository.save(country);
//    }

    public List<Country> createCountries(List<Country> countries) {
        return countryRepository.saveAll(countries);
    }

    public boolean existsCountryByTitle(String title) {
        return countryRepository.existsCountryByTitle(title);
    }

    public List<Country> findAllCountries() {
        return countryRepository.findAll();
    }
}
