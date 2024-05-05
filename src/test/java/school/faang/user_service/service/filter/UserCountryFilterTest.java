package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.UserCountryFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserCountryFilterTest {
    @InjectMocks
    private UserCountryFilter userCountryFilter;
    private UserFilterDto userFilterDto;


    @BeforeEach
    public void init(){
        userFilterDto = CreatingTestData.createUserFilterDtoForTest();
    }

    @Test
    public void testIsApplicableReturnFalse(){
        UserFilterDto userFilterDto =new UserFilterDto();

        assertFalse(userCountryFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicableReturnTrue(){
        assertTrue(userCountryFilter.isApplicable(userFilterDto));
    }
    @Test
    public void testIsApplyUserNameFilter(){
        Country germany = Country.builder()
                .id(2L)
                .title("Germany").build();
        List<User> correctListUser = List.of(User.builder()
                .id(2L)
                .username("Женя")
                .country(germany).build());
        assertEquals(correctListUser, userCountryFilter.apply(CreatingTestData.createListUsers().stream()
                ,userFilterDto).collect(Collectors.toList()));
    }
    @Test
    public void testApplyNullPointerException(){
        assertThrows(NullPointerException.class,() -> userCountryFilter.apply(null,userFilterDto));
    }
}