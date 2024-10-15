package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.util.TestDataFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserPageFilterTest {
    private UserPageFilter pageFilter = new UserPageFilter();


    @Test
    public void givenValidUserWhenApplyThenReturnUser() {
        // Given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            users.add(TestDataFactory.createUser());
        }
        List<User> actual = List.of(users.get(6), users.get(7), users.get(8));

        var filter = TestDataFactory.createFilterDto();
        filter.setPage(2);
        filter.setPageSize(3);

        // When
        var result = pageFilter.apply(users.stream(), filter).toList();

        // Then
        assertEquals(result, actual);
    }
}
