package school.faang.user_service.filter.userPremium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userPremium.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.userPremium.CountryFilterPattern;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CountryFilterPatternTest {
    @InjectMocks
    private CountryFilterPattern countryFilterPattern;
    @Mock
    private UserFilterDto userFilterDto;
    private List<User> allUsers;

    @BeforeEach
    void init() {
        User userOne = new User();
        userOne.setCountry(new Country(1L, "A", null));
        User userTwo = new User();
        userTwo.setCountry(new Country(2L, "B", null));
        allUsers = List.of(userOne, userTwo);
    }

    @Test
    void testCountryFilterPatternNull() {
        userFilterDto = new UserFilterDto();
        boolean result = countryFilterPattern.isApplication(userFilterDto);
        assertFalse(result);
    }

    @Test
    void testCountryFilterPatternTrue() {
        userFilterDto = new UserFilterDto(null, "A");
        boolean result = countryFilterPattern.isApplication(userFilterDto);
        assertTrue(result);
    }

    @Test
    void testApply() {
        Mockito.when(userFilterDto.getCountryFilter()).thenReturn("A");
        User userTest = new User();
        userTest.setCountry(new Country(1L, "A", null));
        List<User> resultUsers = List.of(userTest);

        Stream<User> testUsers = countryFilterPattern.apply(allUsers.stream(), userFilterDto);

        assertEquals(resultUsers, testUsers.toList());
    }
}