package school.faang.user_service.mapper.mentorship;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {

    @Mapping(source = "mentorId", target = "mentor")
    @Mapping(source = "menteeId", target = "mentee")
    Mentorship toMentorship(MentorshipDto dto);

    @Mapping(source = "mentor.id", target = "mentorId")
    @Mapping(source = "mentee.id", target = "menteeId")
    MentorshipDto toDto(Mentorship mentorship);

    default User mapIdToUser(Long id) {
        if (id == null) {
            return null;
        }
        return User.builder()
                .id(id)
                .build();
    }

}
