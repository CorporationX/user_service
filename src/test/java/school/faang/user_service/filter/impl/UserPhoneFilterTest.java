package school.faang.user_service.filter.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterRequestDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class UserPhoneFilterTest {
    private final UserPhoneFilter userPhoneFilter = new UserPhoneFilter();

    @Test
    public void testIsApplicable() {
        boolean actualResult = userPhoneFilter.isApplicable(
                new UserFilterRequestDto(
                        null,
                        "+79999999999",
                        null,
                        null)
        );

        Assertions.assertTrue(actualResult);
    }

    @Test
    public void testIsNotApplicable() {
        boolean actualResult = userPhoneFilter.isApplicable(
                new UserFilterRequestDto(
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

        List<User> actualResult = userPhoneFilter.apply(
                users,
                new UserFilterRequestDto(
                        null,
                        "+79999999999",
                        null,
                        null)
        ).toList();

        Assertions.assertEquals(1, actualResult.size());
    }
}
