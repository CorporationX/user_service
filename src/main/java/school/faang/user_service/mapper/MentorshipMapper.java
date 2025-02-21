package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.users.UserDto;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {

    List<UserDto> toUserDto(List<User> mentees);
}
