package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryDto createCountry(CountryDto countryDto) {
        return countryMapper.toCountryDto(countryRepository.save(countryMapper.toCountry(countryDto)));
    }

    public CountryDto findCountryByTitle(String title) {
        Optional<Country> country = countryRepository.findCountryByTitle(title);
        if (country.isEmpty()) {
            return createCountry(CountryDto.builder().title(title).build());
        }
        return countryMapper.toCountryDto(country.get());
    }
}
