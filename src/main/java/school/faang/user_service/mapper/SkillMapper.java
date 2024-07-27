package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    @Mapping(source = "users", target = "userIds", qualifiedByName = "usersToUserIds")
    SkillDto toDto(Skill skill);

    List<SkillDto> toDto(List<Skill> skills);

    @Mapping(target = "users", ignore = true)
    Skill toEntity(SkillDto skillDto);

    @Named("usersToUserIds")
    default List<Long> usersToUserIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream().map(User::getId).toList();
    }
}
