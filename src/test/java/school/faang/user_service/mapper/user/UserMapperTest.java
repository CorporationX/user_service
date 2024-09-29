package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.student.Address;
import school.faang.user_service.pojo.student.ContactInfo;
import school.faang.user_service.pojo.student.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;

    private static final int SIZE_USER_DTO_LIST = 2;
    private static final int USER_DTOS_SIZE = 2;

    private static final String USER_NAME_ONE = "name";
    private static final String USER_NAME_TWO = "Name";

    private User userOne;
    private User userTwo;
    private List<User> users;

    @BeforeEach
    public void init() {
        userOne = User.builder()
                .id(USER_ID_ONE)
                .username(USER_NAME_ONE)
                .build();

        userTwo = User.builder()
                .id(USER_ID_TWO)
                .username(USER_NAME_TWO)
                .build();
        users = List.of(userOne, userTwo);
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("If gets null than return null")
        void whenListUsersIsNullThenGetNull() {
            assertNull(userMapper.toDtos(null));
        }

        @Test
        @DisplayName("Успех маппинга User в UserDto")
        public void whenUserIsNotNullThenReturnUserToDto() {
            UserDto userDto = userMapper.toDto(userOne);

            assertNotNull(userDto);
            assertEquals(userOne.getId(), userDto.getId());
            assertEquals(userOne.getUsername(), userDto.getUsername());
        }

        @Test
        @DisplayName("When gets List<User> with size 2 than return List<UserDto> with size 2")
        void whenListOfUsersIsNotNullThenGetListOfUserDtos() {
            List<User> users = List.of(
                    User.builder()
                            .id(USER_ID_ONE)
                            .username("User")
                            .email("Email")
                            .build(),
                    User.builder()
                            .id(USER_ID_TWO)
                            .username("User1")
                            .email("Email1")
                            .active(true)
                            .build());

            List<UserDto> userDtos = userMapper.toDtos(users);

            assertEquals(SIZE_USER_DTO_LIST, userDtos.size());
        }

        @Test
        @DisplayName("Успех маппинга List<User> в List<UserDto>")
        public void whenListOfUserIsNotNullThenReturnListUserDtos() {
            List<UserDto> userDtos = userMapper.toDtos(users);

            assertNotNull(userDtos);
            assertEquals(USER_DTOS_SIZE, userDtos.size());
            assertEquals(userOne.getId(), userDtos.get(0).getId());
            assertEquals(userOne.getUsername(), userDtos.get(0).getUsername());
            assertEquals(userTwo.getId(), userDtos.get(1).getId());
            assertEquals(userTwo.getUsername(), userDtos.get(1).getUsername());
        }
    }

    @Nested
    class PojoToEntity {

        private static final String FIRST_NAME = "John";
        private static final String LAST_NAME = "Doe";
        private static final String EMAIL = "johndoe@example.com";
        private static final String PHONE = "+1-123-456-7890";
        private static final String COUNTRY = "USA";

        private static Person person;

        @BeforeEach
        void init() {
            person = Person.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .contactInfo(ContactInfo.builder()
                            .phone(PHONE)
                            .email(EMAIL)
                            .address(Address.builder()
                                    .country(COUNTRY)
                                    .build())
                            .build())
                    .build();
        }

        @Test
        @DisplayName("Assert that fields from Person pojo are equals to User fields")
        void whenPersonPojoMappedToUserThenFieldsShouldBeEquals() {
            User user = userMapper.toEntity(person);

            String expectedUsername = FIRST_NAME + " " + LAST_NAME;

            assertEquals(expectedUsername, user.getUsername());
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PHONE, user.getPhone());
            assertEquals(COUNTRY, user.getCountry().getTitle());
        }
    }
}