package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.UserDto;
import school.faang.user_service.entity.User;

@Mapper(
    componentModel = "spring",
    uses = {EventSkillMapper.class})
public interface EventUserMapper {

  @Mapping(target = "id", source = "userDto.id", ignore = true)
  @Mapping(target = "username", source = "userDto.username")
  @Mapping(target = "email", source = "userDto.email")
  @Mapping(target = "skills", source = "userDto.skills")
  @Mapping(target = "phone", ignore = true)
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "aboutMe", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "city", ignore = true)
  @Mapping(target = "experience", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "followers", ignore = true)
  @Mapping(target = "followees", ignore = true)
  @Mapping(target = "ownedEvents", ignore = true)
  @Mapping(target = "mentees", ignore = true)
  @Mapping(target = "mentors", ignore = true)
  @Mapping(target = "receivedMentorshipRequests", ignore = true)
  @Mapping(target = "sentMentorshipRequests", ignore = true)
  @Mapping(target = "sentGoalInvitations", ignore = true)
  @Mapping(target = "receivedGoalInvitations", ignore = true)
  @Mapping(target = "participatedEvents", ignore = true)
  @Mapping(target = "goals", ignore = true)
  @Mapping(target = "recommendationsGiven", ignore = true)
  @Mapping(target = "recommendationsReceived", ignore = true)
  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "ratings", ignore = true)
  @Mapping(target = "userProfilePic", ignore = true)
  @Mapping(target = "contactPreference", ignore = true)
  @Mapping(target = "premium", ignore = true)
  User toEntity(UserDto userDto);
}
