package school.faang.user_service.filters.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.impl.UserPhoneFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserPhoneFilterTest {
    private final UserPhoneFilter filter = new UserPhoneFilter();
    private UserFilterDto filterDto;

    private User user1;
    private User user2;

    Stream<User> stream;

    @BeforeEach
    public void init() {
        filterDto = new UserFilterDto();
        user1 = User.builder().phone("1111111").build();
        user2 = User.builder().phone("1222222").build();
        stream = Stream.of(user1, user2);
    }

    @Test
    public void testApplySuccessCase() {
        filterDto.setPhonePattern("1222222");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(user2, actual.get(0));
    }

    @Test
    public void testApplyCaseWithNotFullString() {
        filterDto.setPhonePattern("122");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
    }

    @Test
    public void testApplyWithPhonePatternNull() {
        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }

    @Test
    public void testApplyWithBlankString() {
        filterDto.setPhonePattern("");

        List<User> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
    }
}
