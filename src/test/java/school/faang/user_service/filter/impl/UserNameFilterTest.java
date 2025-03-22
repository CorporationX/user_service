package school.faang.user_service.filter.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserNameFilterTest {
    private final UserNameFilter userNameFilter = new UserNameFilter();

    @Test
    public void testIsApplicable() {
        boolean actualResult = userNameFilter.isApplicable(
                new UserFilterRequest(
                        "Takewqa",
                        null,
                        null,
                        null)
        );

        Assertions.assertTrue(actualResult);
    }

    @Test
    public void testIsNotApplicable() {
        boolean actualResult = userNameFilter.isApplicable(
                new UserFilterRequest(
                        null,
                        null,
                        null,
                        null)
        );

        Assertions.assertFalse(actualResult);
    }

    @Test
    public void testApply() {
        Stream<User> users = Stream.of(
                User.builder().phone("+79999999999").username("Takewqa").experience(222).build(),
                User.builder().phone("+79999999997").username("Aqwekat").experience(222).build());

        List<User> actualResult = userNameFilter.apply(
                users,
                new UserFilterRequest(
                        "Aqwekat",
                        null,
                        null,
                        null)
        ).toList();

        Assertions.assertEquals(1, actualResult.size());
    }
}
