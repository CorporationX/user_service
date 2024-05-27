package school.faang.user_service.cache.collector;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.repository.CountryDtoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CollectCountryFromDB {
    private final CountryDtoRepository countryRepository;
    private final HashMapCountry hashMapCountry;

    @Transactional
    public void collectCountry(){
        List<CountryDto> countryDtos = countryRepository.findAll();
        hashMapCountry.addCountry(countryDtos);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        collectCountry();
    }
}