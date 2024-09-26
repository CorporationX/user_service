package school.faang.user_service.service.country;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    private static Country country;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("WhenEntityFoundThenNotThrownException")
        void whenEntityFoundThenNotThrownException() {
            when(countryRepository.findByTitle(anyString())).thenReturn(Optional.of(new Country()));

            countryService.findCountryByTitle(anyString());

            verify(countryRepository).findByTitle(anyString());
        }

        @Test
        @DisplayName("WhenEntityIsCorrectThenNotThrownException")
        void whenEntityIsCorrectThenSave() {
            country = Country.builder()
                    .title("TEST")
                    .build();

            countryService.saveCountry(country);

            verify(countryRepository).save(country);
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("WhenEntityNotFoundThenThrowEntityNotFoundException")
        void whenEntityNotFoundThenThrowsEntityNotFoundException() {
            when(countryRepository.findByTitle(anyString())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> countryService.findCountryByTitle(anyString()));
        }
    }
}