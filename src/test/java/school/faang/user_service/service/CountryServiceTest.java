package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.user.CountryService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    CountryRepository countryRepository;

    @InjectMocks
    CountryService countryService;

    @Test
    void testCreateCountriesIsSuccessful(){

        countryService.createCountries(Mockito.any());

        verify(countryRepository, times(1)).saveAll(Mockito.any());
    }

    @Test
    void testExistsCountryIsSuccessful(){

        countryService.existsCountryByTitle(anyString());

        verify(countryRepository, times(1)).existsCountryByTitle(anyString());
    }

    @Test
    void testFindAllCountriesIsSuccessful(){

        countryService.findAllCountries();

        verify(countryRepository, times(1)).findAll();
    }
}