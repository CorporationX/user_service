package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SkillMapper {
    public static final SkillMapper INSTANCE = Mappers.getMapper(SkillMapper.class);

//    @Mapping(target = "users", source = "userIds", qualifiedByName = "UsersToIds")
    Skill toEntity(SkillDto skillDto);

//    @Mapping(target = "userIds", source = "users", qualifiedByName = "UsersToIds")
    SkillDto toDto(Skill skill);

//    @Named("UsersToIds")
//    public static List<Long> convertUsersToIds(List<User> users) {
//        return users.stream().map(User::getId).toList();
//    }
}
