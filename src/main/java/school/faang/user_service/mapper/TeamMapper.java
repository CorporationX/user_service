package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.team.TeamDto;
import school.faang.user_service.entity.team.Team;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TeamMapper {
    @Mapping(target = "avatarUrl", expression = "java(team.getAvatarUrl() != null ? \"/api/teams/\" + team.getId() + \"/avatar\" : null)")
    TeamDto toTeamDto(Team team);
}
