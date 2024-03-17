package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD
)
public interface SkillMapper {

    @Mapping(target = "users", ignore = true)
    Skill toEntity (SkillDto skillDto);

    @Mapping(target = "userIds", source = "users", qualifiedByName = "usersToIds")
    SkillDto toDto (Skill skill);

    List<SkillDto> listToDto(List<Skill> skills);

    @Named("usersToIds")
    default List<Long> convertUsersToIds (List<User> users) {
        return users != null
        ? users.stream().map(User::getId).toList()
        : Collections.emptyList();
    }
}
