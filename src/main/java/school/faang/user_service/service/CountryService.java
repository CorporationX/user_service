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

    public Country getCountryByTitle (String title) {
        Country country = countryRepository.findByTitle(title);
        if (country == null) {
            country = Country.builder()
                    .title(title)
                    .build();
            countryRepository.save(country);
        }
        return country;
    }
}
