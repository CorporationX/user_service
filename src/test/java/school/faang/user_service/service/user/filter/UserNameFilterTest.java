package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserNameFilterTest {
    private final UserNameFilter nameFilter = new UserNameFilter();

    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .username("MichaelJohnson")
                        .build(),
                User.builder()
                        .username("JohnDoe")
                        .build(),
                User.builder()
                        .username("JaneSmith")
                        .build()
        );
    }

    @Test
    public void shouldReturnTrueIfFilterIsSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("John")
                .build();

        boolean isApplicable = nameFilter.isApplicable(filters);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void shouldReturnFalseIfFilterIsSpecified() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = nameFilter.isApplicable(filters);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void shouldReturnFilteredUsersList() {
        UserFilterDto filters = UserFilterDto.builder()
                .namePattern("John")
                .build();
        List<User> desireUsers = List.of(
                User.builder()
                        .username("MichaelJohnson")
                        .build(),
                User.builder()
                        .username("JohnDoe")
                        .build()
        );

        Stream<User> receivedUsers = nameFilter.apply(usersStream.stream(), filters);

        Assertions.assertEquals(desireUsers, receivedUsers.toList());
    }
}
