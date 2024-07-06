package school.faang.user_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MentorshipMapper {
    @Mapping(source="mentor",target = "mentorId",qualifiedByName = "mentorFunc")
    @Mapping(source="mentee",target = "menteeId",qualifiedByName = "menteeFunc")
    MentorshipDto toDto(Mentorship mentorship);

    Mentorship toEntity(MentorshipDto mentorshipDto);

    @Named("mentorFunc")
    default Long mentorFunc(User mentor) {
        return mentor.getId();
    }

    @Named("menteeFunc")
    default Long menteeFunc(User mentor) {
        return mentor.getId();
    }
}
