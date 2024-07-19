package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    @Mapping(source = "users", target = "userIds", qualifiedByName = "map")
    SkillDto skillToDto(Skill skill);

    List<SkillDto> listSkillToDto(List<Skill> skills);

    @Named("map")
    default List<Long> map(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    Skill toEntity(SkillDto skillDto);
}
