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
public class UserMaxExperienceFilterTest {
    private final UserMaxExperienceFilter userMaxExperienceFilter = new UserMaxExperienceFilter();

    @Test
    public void testIsApplicable() {
        boolean actualResult = userMaxExperienceFilter.isApplicable(
                new UserFilterRequestDto(
                        null,
                        null,
                        null,
                        12)
        );

        Assertions.assertTrue(actualResult);
    }

    @Test
    public void testIsNotApplicable() {
        boolean actualResult = userMaxExperienceFilter.isApplicable(
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
                User.builder().phone("+79999999997").username("Aqwekat").experience(123).build());

        List<User> actualResult = userMaxExperienceFilter.apply(
                users,
                new UserFilterRequestDto(
                        null,
                        null,
                        null,
                        124)
        ).toList();

        Assertions.assertEquals(1, actualResult.size());
    }
}
