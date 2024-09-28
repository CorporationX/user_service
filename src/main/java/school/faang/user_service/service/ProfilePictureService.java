package school.faang.user_service.service;

import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.entity.UserProfilePic;

public interface ProfilePictureService {

    UserProfilePic saveProfilePictures(UserRegistrationDto userRegistrationDto);
}
