package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper
public interface MentorshipRequestMapper {
    MentorshipRequestMapper INSTANCE = Mappers.getMapper(MentorshipRequestMapper.class);

    MentorshipRequest toEntity(MentorshipRequestDto dto);
    MentorshipRequestDto toDto(MentorshipRequest entity);
}
