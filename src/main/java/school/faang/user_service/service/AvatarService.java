package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

@Service
@RequiredArgsConstructor
public class AvatarService {

    @Value("${services.diceBear.avatar_url.full}")
    @Setter
    private String fullAvatarUrl;

    @Value("${services.diceBear.avatar_url.small}")
    @Setter
    private String smallAvatarUrl;


    public void setDefaultUserAvatar(User user) {
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fullAvatarUrl + user.hashCode())
                .smallFileId(smallAvatarUrl + user.hashCode())
                .build());
    }
}
