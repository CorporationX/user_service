package school.faang.user_service.cache;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.country.CountryDto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HashMapCountry {
    private ConcurrentHashMap<String, CountryDto> countryMap = new ConcurrentHashMap<>();

    public boolean isContainsKey(String key) {
        return countryMap.containsKey(key);
    }

    public void addCountry(List<CountryDto> countryDtos) {
        countryDtos.forEach(country -> countryMap.put(country.getTitle(), country));
    }

    public void addCountry(CountryDto countryDto) {
        countryMap.put(countryDto.getTitle(), countryDto);
    }

    public CountryDto findCountryByTitle(String title) {
        return countryMap.get(title);
    }
}
