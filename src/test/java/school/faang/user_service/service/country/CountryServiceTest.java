package school.faang.user_service.service.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @Mock
    private HashMapCountry hashMapCountry;

    private CountryDto countryDto;
    private User user;
    private Country country;

    @BeforeEach
    public void setUp() {
        countryDto = CountryDto.builder().id(1L).title("title").build();
        country = Country.builder().id(1L).title("title").build();
        user = User.builder().country(country).build();
    }

    @Test
    public void testSaveCountry() {
        when(countryRepository.save(any(Country.class))).thenReturn(country);
        when(countryMapper.toDto(any(Country.class))).thenReturn(countryDto);
        doNothing().when(hashMapCountry).addCountry(any(CountryDto.class));

        countryService.saveCountry(country, user);

        verify(countryRepository, times(1)).save(country);
        verify(countryMapper, times(1)).toDto(country);
        verify(hashMapCountry, times(1)).addCountry(countryDto);
    }

    @Test
    public void testSetCountryForUser() {
        when(hashMapCountry.findCountryByTitle(anyString())).thenReturn(countryDto);
        when(countryMapper.toEntity(any(CountryDto.class))).thenReturn(country);

        User result = countryService.setCountryForUser(user);

        verify(hashMapCountry, times(1)).findCountryByTitle(anyString());
        verify(countryMapper, times(1)).toEntity(countryDto);
        assertEquals(country, result.getCountry());
    }
}
