package school.faang.user_service.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.service.country.CountryService;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CountryStorage {

    public static ConcurrentHashMap<String, Country> countryCache = new ConcurrentHashMap<>();

    private final CountryService countryService;
    @PostConstruct
    void init() {
        countryService.fillCountryStorage();
    }

}
