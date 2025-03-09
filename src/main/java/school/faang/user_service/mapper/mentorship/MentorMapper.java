package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorMapper {
    User toEntity(MentorDto mentorDto);

    MentorDto toDto(User user);
}
