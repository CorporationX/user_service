package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(); // Создание нового пользователя
        Country userCountry = new Country();
        userCountry.setTitle("TestCountry");
        user.setCountry(userCountry);
    }

    @Test
    public void testGetCountryOrCreateByName_WhenCountryExists() {
        Country existingCountry = new Country();
        existingCountry.setTitle("TestCountry");
        when(countryRepository.findByTitleIgnoreCase("TestCountry")).thenReturn(Optional.of(existingCountry));

        Country resultCountry = countryService.getCountryOrCreateByName(user);

        assertNotNull(resultCountry);
        assertEquals("TestCountry", resultCountry.getTitle());
        verify(countryRepository, never()).save(any());
    }

}