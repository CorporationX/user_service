package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.promotion.user.UserToPromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface UserToPromotionMapper {
    List<UserToPromotionDto> toDtoList(List<User> users);
}
