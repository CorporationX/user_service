package school.faang.user_service.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MenteeMapper {
    MenteeMapper INSTANCE = Mappers.getMapper(MenteeMapper.class);

    @Mapping(source = "mentee.name", target = "name")
    MenteeDTO menteeToMenteeDTO(User mentee);

    @Mapping(source = "menteeDTO.name", target = "name")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    MenteeDTO menteeDTOToMentee(MenteeDTO menteeDTO);
}