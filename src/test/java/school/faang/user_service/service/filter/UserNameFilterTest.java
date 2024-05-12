package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.UserNameFilter;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserNameFilterTest {
    @InjectMocks
    private UserNameFilter userNameFilter;
    private UserFilterDto userFilterDto;


    @BeforeEach
    public void init(){
        userFilterDto = CreatingTestData.createUserFilterDtoForTest();
    }

    @Test
    public void testIsApplicableReturnFalse(){
        UserFilterDto userFilterDto =new UserFilterDto();

        assertFalse(userNameFilter.isApplicable(userFilterDto));
    }

    @Test
    public void testIsApplicableReturnTrue(){
        assertTrue(userNameFilter.isApplicable(userFilterDto));
    }
    @Test
    public void testIsApplyUserNameFilter(){
        List<User> correctListUser = List.of(User.builder()
                .id(1L)
                .username("Катя")
                .build());
        assertEquals(correctListUser, userNameFilter.apply(CreatingTestData.createListUsersNonCountry().stream()
                ,userFilterDto).collect(Collectors.toList()));
    }
    @Test
    public void testApplyNullPointerException(){
        assertThrows(NullPointerException.class,() -> userNameFilter.apply(null,userFilterDto));
    }
}