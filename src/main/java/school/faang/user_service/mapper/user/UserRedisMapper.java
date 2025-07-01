package school.faang.user_service.mapper.user;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.user.UserRedisModel;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface UserRedisMapper {

    UserRedisModel toUserRedisModel(User user);

    User toUserEntity(UserRedisModel userRedisModel);
}