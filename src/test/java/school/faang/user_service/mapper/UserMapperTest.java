package school.faang.user_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.constants.Constants.PATTERN_OF_DATE;
import static school.faang.user_service.constants.Constants.PREMIUM_STATUS_ACTION;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

  private static final int NUMBER_OF_POSITIVE_DAY = 1;
  private static final int NUMBER_OF_NEGATIVE_DAY = -1;

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  private User createUserDtoWitchPremium(LocalDateTime date) {
    return User.builder()
        .id(1L)
        .username("Alex")
        .email("firstEmail")
        .phone("firstPhone")
        .aboutMe("firstAboutMe")
        .city("firstCity")
        .active(true)
        .premium(Premium.builder()
            .id(1L)
            .user(User.builder()
                .id(1L)
                .build())
            .endDate(date)
            .build())
        .build();
  }

  @Test
  @DisplayName("Проверка конвертации из списка User в список UserDto")
  public void convertListUserForListUserDto() {
    User firstUser = User.builder()
        .id(1L)
        .username("Alex")
        .email("firstEmail")
        .phone("firstPhone")
        .aboutMe("firstAboutMe")
        .city("firstCity")
        .active(true)
        .premium(null)
        .build();

    User secondUser = User.builder()
        .id(2L)
        .username("Andry")
        .email("secondEmail")
        .phone("secondPhone")
        .aboutMe("secondAboutMe")
        .city("secondCity")
        .active(true)
        .premium(null)
        .build();

    List<User> userList = List.of(firstUser, secondUser);

    UserDto firstUserDto = UserDto.builder()
        .id(1L)
        .username("Alex")
        .email("firstEmail")
        .phone("firstPhone")
        .aboutMe("firstAboutMe")
        .city("firstCity")
        .active(true)
        .premium("")
        .build();

    UserDto secondUserDto = UserDto.builder()
        .id(2L)
        .username("Andry")
        .email("secondEmail")
        .phone("secondPhone")
        .aboutMe("secondAboutMe")
        .city("secondCity")
        .active(true)
        .premium("")
        .build();

    List<UserDto> userListDto = List.of(firstUserDto, secondUserDto);

    List<UserDto> actualResult = userMapper.toDtoList(userList);

    assertThat(actualResult).isEqualTo(userListDto);
  }

  @Test
  @DisplayName("Проверка конвертации из User в UserDto")
  public void convertUserForUserDto() {
    User user = User.builder()
        .id(1L)
        .username("Alex")
        .email("firstEmail")
        .phone("firstPhone")
        .aboutMe("firstAboutMe")
        .city("firstCity")
        .active(true)
        .premium(null)
        .build();

    UserDto userDto = UserDto.builder()
        .id(1L)
        .username("Alex")
        .email("firstEmail")
        .phone("firstPhone")
        .aboutMe("firstAboutMe")
        .city("firstCity")
        .active(true)
        .premium("")
        .build();

    UserDto actualResult = userMapper.toUserDto(user);

    assertThat(userDto).isEqualTo(actualResult);
  }

  @Test
  @DisplayName("Проверка актуального премиума у пользователя при конвертации")
  public void convertUserForUserDtoWithActualPremium() {
    LocalDateTime date = getDate(NUMBER_OF_POSITIVE_DAY);
    User user = createUserDtoWitchPremium(date);

    UserDto actualResult = userMapper.toUserDto(user);

    assertThat(actualResult.getPremium()).isEqualTo(String.format(PREMIUM_STATUS_ACTION,
        date.format(DateTimeFormatter.ofPattern(PATTERN_OF_DATE))));
  }

  @Test
  @DisplayName("Проверка неактуального премиума у пользователя при конвертации")
  public void convertUserForUserDtoWithNotActualPremium() {
    User user = createUserDtoWitchPremium(getDate(NUMBER_OF_NEGATIVE_DAY));

    UserDto actualResult = userMapper.toUserDto(user);

    assertThat(actualResult.getPremium()).isEqualTo("");
  }

  private LocalDateTime getDate(int day) {
    return LocalDateTime.now().plusDays(day);
  }

}