package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {
    List<MenteeDto> menteesToMenteesDtos(List<User> users);
    List<MentorDto> mentorsToMentorsDtos(List<User> users);
}
