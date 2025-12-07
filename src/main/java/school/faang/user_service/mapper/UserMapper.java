package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.picture.PictureDto;
import school.faang.user_service.dto.picture.PictureType;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.s3.S3service;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected S3service s3service;

    public abstract User toUser(CreateUserDto userDto);

    public abstract void update(UpdateUserDto userDto, @MappingTarget User entity);

    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(target = "followersIds", expression = "java(mapFollowers(user))")
    @Mapping(target = "pictures", source = "user", qualifiedByName = "mapPictures")
    public abstract UserDto toUserDto(User user);

    public abstract List<UserDto> toUserDtos(List<User> users);

    protected List<Long> mapFollowers(User user) {
        return user.getFollowers().stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapPictures")
    protected List<PictureDto> mapPictures(User user) {
        List<PictureDto> pictures = new ArrayList<>();
        UserProfilePic pic = user.getUserProfilePic();

        if (pic != null) {
            pictures.add(new PictureDto(getUrl(pic.getFileId()), PictureType.AVATAR_MEDIUM));
            pictures.add(new PictureDto(getUrl(pic.getSmallFileId()), PictureType.AVATAR_SMALL));
        }
        return pictures;
    }

    private String getUrl(String fileId) {
        if (fileId == null) {
            return null;
        }
        // Если это уже готовый URL (от Dicebear), просто возвращаем его.
        // Если это ключ S3, генерируем URL.
        return fileId.startsWith("http") ? fileId : s3service.getUrl(fileId);
    }
}