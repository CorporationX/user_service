package school.faang.user_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
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

  private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  private User createUserDtoWitchPremium(Month month) {
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
            .endDate(LocalDateTime.of(2024, month, 01, 00, 00))
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
    User user = createUserDtoWitchPremium(Month.SEPTEMBER);

    UserDto actualResult = userMapper.toUserDto(user);

    assertThat(actualResult.getPremium()).isEqualTo("Имеется премиум подписка, которая действует до 01.09.2024 года.");
  }

  @Test
  @DisplayName("Проверка неактуального премиума у пользователя при конвертации")
  public void convertUserForUserDtoWithNotActualPremium() {
    User user = createUserDtoWitchPremium(Month.JANUARY);

    UserDto actualResult = userMapper.toUserDto(user);

    assertThat(actualResult.getPremium()).isEqualTo("");
  }

}