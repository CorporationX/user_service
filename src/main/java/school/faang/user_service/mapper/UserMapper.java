package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.CountryRepository;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    public abstract UserDto toDto(User user);

    public abstract List<UserDto> toDtoList(List<User> users);

    public abstract List<UserDto> listToDto(List<User> users);

    public abstract User toEntity(UserDto userDto);

    @Mapping(target = "userProfilePic", expression = "java(getUserProfilePic(userDto))")
    @Mapping(target = "country", ignore = true)
    public abstract User toEntity(UserRegistrationDto userDto);

    protected UserProfilePic getUserProfilePic(UserRegistrationDto userDto) {
        String fileId = userDto.getProfilePicFileId();
        String smallFileId = userDto.getProfilePicSmallFileId();

        if (fileId != null && smallFileId != null) {
            return UserProfilePic.builder()
                    .fileId(fileId)
                    .smallFileId(smallFileId)
                    .build();
        }
        return null;
    }
}