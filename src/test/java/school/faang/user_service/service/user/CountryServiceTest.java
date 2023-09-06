package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @InjectMocks
    private CountryService countryService;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountryMapper countryMapper;

    @Test
    public void testCreateCountry() {
        CountryDto countryDto = CountryDto.builder()
                .title("test")
                .build();
        Country country = new Country();

        Mockito.when(countryMapper.toEntity(countryDto))
                .thenReturn(country);
        Mockito.when(countryRepository.save(country))
                .thenReturn(country);
        Mockito.when(countryMapper.toDto(country))
                .thenReturn(countryDto);

        countryService.create(countryDto);

        Mockito.verify(countryMapper, Mockito.times(1))
                .toEntity(countryDto);
        Mockito.verify(countryRepository, Mockito.times(1))
                .save(country);
        Mockito.verify(countryMapper, Mockito.times(1))
                .toDto(country);
    }

    @Test
    public void testGetIdByTitle() {
        String title = "test";
        countryService.getIdByTitle(title);
        Mockito.verify(countryRepository, Mockito.times(1))
                .findIdByTitle(title);
    }
}