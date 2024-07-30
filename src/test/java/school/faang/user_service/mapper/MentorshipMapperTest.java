package school.faang.user_service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;

class MentorshipMapperTest {

  private final MentorshipMapper mapper = Mappers.getMapper(MentorshipMapper.class);

  public static User generateUser() {
    return User.builder()
        .id(1)
        .username("user name")
        .email("userName@mail.com")
        .phone("123456789")
        .aboutMe("about user")
        .city("user city")
        .build();
  }

  public static MentorshipDto generateMentorshipDto() {
    return MentorshipDto.builder()
        .id(1)
        .username("user name")
        .email("userName@mail.com")
        .phone("123456789")
        .aboutMe("about user")
        .city("user city")
        .build();
  }

  @Test
  @DisplayName("Проверка равенства User и MentorshipDto после маппинга")
  void testConverterMentorshipDtoFromUser() {
    final var mentorshipDto = mapper.mentorshipDtoFromUser(generateUser());
    assertThat(mentorshipDto).isEqualTo(generateMentorshipDto());
  }

}
