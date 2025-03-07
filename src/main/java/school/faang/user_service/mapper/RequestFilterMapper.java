package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface RequestFilterMapper {
    MentorshipRequest toEntity(RequestFilterDto requestFilterDto);

    RequestFilterDto toDto(MentorshipRequest mentorshipRequest);
}
