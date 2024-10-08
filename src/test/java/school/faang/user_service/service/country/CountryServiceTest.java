package school.faang.user_service.service.country;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    private static final String COUNTRY_NAME = "TEST";

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    @DisplayName("When entity found then not thrown exception")
    void whenEntityFoundThenSuccess() {
        when(countryRepository.findByTitle(anyString()))
                .thenReturn(Optional.of(new Country()));

        countryService.findCountryByTitle(anyString());

        verify(countryRepository).findByTitle(anyString());
    }

    @Test
    @DisplayName("When country is exists by title then return country")
    void whenEntityFoundThenReturnEntity() {
        when(countryRepository.findByTitle(COUNTRY_NAME))
                .thenReturn(Optional.of(Country.builder().build()));

        countryService.findCountryAndSaveIfNotExists(COUNTRY_NAME);

        verify(countryRepository).findByTitle(COUNTRY_NAME);
    }

    @Test
    @DisplayName("When country is not exists by title then save and return country")
    void whenEntityNotFoundThenSaveAndReturnEntity() {
        when(countryRepository.findByTitle(anyString()))
                .thenReturn(Optional.empty());

        countryService.findCountryAndSaveIfNotExists(anyString());

        verify(countryRepository).save(any());
    }
}
