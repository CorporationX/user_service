package school.faang.user_service.mapper.mentorship;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = MenteeMapper.class)
public interface MentorMapper {
    MentorDto toDto(User user);
}
