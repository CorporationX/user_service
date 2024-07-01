package school.faang.user_service.service.country;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final HashMapCountry hashMapCountry;

    @Transactional
    public User saveCountry(Country country, User user) {
        Country countrySaved = countryRepository.save(country);
        CountryDto countryDto = countryMapper.toDto(countrySaved);
        hashMapCountry.addCountry(countryDto);
        return setCountryForUser(user);
    }

    @Transactional
    public User setCountryForUser(User user) {
        CountryDto country = hashMapCountry.findCountryByTitle(user.getCountry().getTitle());
        Country userCountry = countryMapper.toEntity(country);
        user.setCountry(userCountry);
        return user;
    }
}
