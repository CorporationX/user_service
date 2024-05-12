package school.faang.user_service.service.filter;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;

public class CreatingTestData {
    public static UserFilterDto createUserFilterDtoForTest() {
        return UserFilterDto.builder().userName("Катя").countryName("Germany").build();
    }

    public static List<User> createListUsers() {
        Country singapore = Country.builder()
                .id(1L)
                .title("Singapore").build();
        Country germany = Country.builder()
                .id(2L)
                .title("Germany").build();
        User firstUser = User.builder()
                .id(1L)
                .username("Катя")
                .country(singapore).build();
        User secondUser = User.builder()
                .id(2L)
                .username("Женя")
                .country(germany).build();
        return List.of(firstUser, secondUser);
    }
    public static List<User> createListUsersNonCountry() {
                User firstUser = User.builder()
                .id(1L)
                .username("Катя").build();
        User secondUser = User.builder()
                .id(2L)
                .username("Женя").build();
        return List.of(firstUser, secondUser);
    }

}
