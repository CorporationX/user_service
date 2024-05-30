package school.faang.user_service.cache.collector;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CollectCountryFromDB {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final HashMapCountry hashMapCountry;

    @Transactional
    public void collectCountry(){
        List<Country> country = countryRepository.findAll();
        List<CountryDto> ListCountryDto = country.stream().map(countryMapper::toDto).toList();

        hashMapCountry.addCountry(ListCountryDto);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        collectCountry();
    }
}