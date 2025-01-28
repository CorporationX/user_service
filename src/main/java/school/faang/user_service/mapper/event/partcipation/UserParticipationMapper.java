package school.faang.user_service.mapper.event.partcipation;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.participant.UserParticipationDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserParticipationMapper {
    User toEntity(UserParticipationDto userParticipationDto);

    UserParticipationDto toDto(User user);
    List <UserParticipationDto> toDtoList(List<User> userList);
}