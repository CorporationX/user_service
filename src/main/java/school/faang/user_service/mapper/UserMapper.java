package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "userProfilePic.fileId", target = "picId")
    @Mapping(source = "userProfilePic.smallFileId", target = "smallPicId")

    UserDto toDto(User user);

    @Mapping(source = "picId", target = "userProfilePic.fileId")
    @Mapping(source = "smallPicId", target = "userProfilePic.smallFileId")
    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);
}
