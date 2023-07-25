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
public class UserAboutFilterTest {
    private final UserAboutFilter aboutFilter = new UserAboutFilter();

    private List<User> usersStream;

    @BeforeEach
    public void initFilter() {
        usersStream = List.of(
                User.builder()
                        .aboutMe("I like Java")
                        .build(),
                User.builder()
                        .aboutMe("I like Python")
                        .build(),
                User.builder()
                        .aboutMe("I like Java")
                        .build()
        );
    }

    @Test
    public void shouldReturnTrueIfFilterIsSpecified() {
        UserFilterDto filters = UserFilterDto.builder()
                .aboutPattern("Java")
                .build();

        boolean isApplicable = aboutFilter.isApplicable(filters);

        Assertions.assertTrue(isApplicable);
    }

    @Test
    public void shouldReturnFalseIfFilterIsSpecified() {
        UserFilterDto filters = new UserFilterDto();

        boolean isApplicable = aboutFilter.isApplicable(filters);

        Assertions.assertFalse(isApplicable);
    }

    @Test
    public void shouldReturnFilteredUsersList() {
        UserFilterDto filters = UserFilterDto.builder()
                .aboutPattern("Java")
                .build();
        List<User> desireUsers = List.of(
                User.builder()
                        .aboutMe("I like Java")
                        .build(),
                User.builder()
                        .aboutMe("I like Java")
                        .build()
        );

        Stream<User> receivedUsers = aboutFilter.apply(usersStream.stream(), filters);

        Assertions.assertEquals(desireUsers, receivedUsers.toList());
    }
}
