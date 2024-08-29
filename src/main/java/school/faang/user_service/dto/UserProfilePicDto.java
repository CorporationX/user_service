package school.faang.user_service.dto;

import school.faang.user_service.entity.UserProfilePic;

public record UserProfilePicDto(
        String fileId,
        String smallFileId
) {
    public static UserProfilePicDto fromUserProfilePic(UserProfilePic userProfilePic) {
        return new UserProfilePicDto(userProfilePic.getFileId(), userProfilePic.getSmallFileId());
    }
}
