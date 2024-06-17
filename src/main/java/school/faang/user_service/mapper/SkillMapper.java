package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    @Mapping(target = "users", ignore = true)
    Skill dtoToSkill(SkillDto skillDto);

    @Mapping(source = "users", target = "userIds", qualifiedByName = "usersToIds")
    SkillDto skillToDto(Skill skill);

    List<SkillDto> skillToDto(List<Skill> skills);

    @Named("usersToIds")
    default List<Long> convertUsersToIds (List<User> users) {
        if(users == null) {
            return List.of();
        }

        return users.stream()
                .map(User::getId)
                .toList();
    }
}
