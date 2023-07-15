package school.faang.user_service.mapper.mentee;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.mentee.MenteeDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenteeMapper {
    MenteeMapper INSTANCE = Mappers.getMapper(MenteeMapper.class);

    List<MenteeDto> toUserDto(List<User> userList);
}
