package school.faang.user_service.service.country;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;

import static school.faang.user_service.storage.CountryStorage.countryCache;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @PostConstruct
    public void fillCountryStorage() {
        List<Country> countryList = countryRepository.findAll();
        countryList.forEach(country -> {
            countryCache.put(country.getTitle(), country);
        });
    }

    public Country getCountryFromCache(User user) {
        return countryCache.computeIfAbsent(user.getCountry().getTitle(), key -> countryRepository.save(user.getCountry()));
    }
}
