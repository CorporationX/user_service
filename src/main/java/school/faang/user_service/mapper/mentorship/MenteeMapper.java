package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.GetMenteesResponse;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MenteeMapper {

    GetMenteesResponse toDto(User user);
}