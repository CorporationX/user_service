package school.faang.user_service.service;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.User;

@Mapper
public interface MenteeMapper {
    MenteeMapper INSTANCE = Mappers.getMapper(MenteeMapper.class);

    MenteeDTO menteeToMenteeDTO(User mentee);
    User menteeDTOToMentee(MenteeDTO menteeDTO);
}