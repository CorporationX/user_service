package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {
    @Mapping(source="mentor.id",target = "mentorId")
    @Mapping(source="mentee.id",target = "menteeId")
    MentorshipDto toDto(Mentorship mentorship);

    Mentorship toEntity(MentorshipDto mentorshipDto);

}
