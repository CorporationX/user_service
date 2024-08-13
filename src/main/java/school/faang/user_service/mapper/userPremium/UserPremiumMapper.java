package school.faang.user_service.mapper.userPremium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.userDto.UserPremiumDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPremiumMapper {
    @Mapping(source = "followers", target = "followersId", qualifiedByName = "listToId")
    @Mapping(source = "mentees", target = "menteesId", qualifiedByName = "listToId")
    @Mapping(source = "goals", target = "goalsId", qualifiedByName = "goalsToId")
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "skillsToId")
    @Mapping(source = "premium.id", target = "premiumId")
    UserPremiumDto toDto(User user);

    User toEntity(UserPremiumDto userDto);

    @Named("listToId")
    default List<Long> listToId(List<User> users) {
        return users.stream().map(User::getId).toList();
    }

    @Named("goalsToId")
    default List<Long> goalsToId(List<Goal> users) {
        return users.stream().map(Goal::getId).toList();
    }

    @Named("skillsToId")
    default List<Long> skillsToId(List<Skill> users) {
        return users.stream().map(Skill::getId).toList();
    }
}
