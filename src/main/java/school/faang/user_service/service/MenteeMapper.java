package school.faang.user_service.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenteeMapper {
    MenteeMapper INSTANCE = Mappers.getMapper(MenteeMapper.class);

    MenteeDTO menteeToMenteeDTO(Mentee mentee);
}