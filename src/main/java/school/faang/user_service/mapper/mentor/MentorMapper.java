package school.faang.user_service.mapper.mentor;


import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentor.MentorDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorMapper {
    List<MentorDto> toMentorListDto (List<User> userList);
    MentorDto toMentorDto (User user);
}
