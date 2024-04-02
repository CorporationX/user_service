package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.image.ImageResizer;
import school.faang.user_service.mapper.resource.ResourceMapperImpl;
import school.faang.user_service.repository.ResourceRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.resource.ResourceService;
import school.faang.user_service.validation.user.UserAvatarValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ResourceService resourceService;
    @Spy
    ResourceMapperImpl resourceMapper;
    @Mock
    UserAvatarValidator userAvatarValidator;
    @Mock
    ResourceRepository resourceRepository;
    @Mock
    ImageResizer imageResizer;
    @InjectMocks
    UserAvatarService userAvatarService;

    private User user;
    private Resource avatar;
    private Resource smallAvatar;
    private MultipartFile avatarFile;
    private MultipartFile smallAvatarFile;
    private UserProfilePic userProfilePic;

    @BeforeEach
    void setUp() {
        avatarFile = new MockMultipartFile("avatar", "avatar.png", "image/png", "avatar".getBytes());
        smallAvatarFile = new MockMultipartFile("avatarSmall", "avatarSmall.png", "image/png", "avatarSmall".getBytes());
        avatar = Resource.builder()
                .id(1L)
                .key(avatarFile.getOriginalFilename())
                .build();
        smallAvatar = Resource.builder()
                .id(2L)
                .key(smallAvatarFile.getOriginalFilename())
                .build();
        userProfilePic = UserProfilePic.builder()
                .fileId(avatar.getKey())
                .smallFileId(smallAvatar.getKey())
                .build();
        user = User.builder()
                .id(10L)
                .userProfilePic(userProfilePic)
                .build();
    }

    @Test
    void upload_AvatarUploadedAndSetAsUserProfilePic_ThenReturnedAsDto() {
        String folderName = "users_avatars/" + user.getId();
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(imageResizer.resize(avatarFile, 1080, 1080)).thenReturn(avatarFile);
        when(imageResizer.resize(avatarFile, 170, 170)).thenReturn(smallAvatarFile);
        when(resourceService.uploadFile(avatarFile, folderName)).thenReturn(avatar);
        when(resourceService.uploadFile(smallAvatarFile, folderName)).thenReturn(smallAvatar);
        when(userRepository.save(user)).thenReturn(user);
        when(resourceRepository.saveAll(List.of(avatar, smallAvatar))).thenReturn(List.of(avatar, smallAvatar));

        List<ResourceDto> returned = userAvatarService.upload(user.getId(), avatarFile);

        assertAll(
                () -> verify(userAvatarValidator, times(1)).validateIfAvatarIsImage(avatarFile.getContentType()),
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(imageResizer, times(1)).resize(avatarFile, 1080, 1080),
                () -> verify(imageResizer, times(1)).resize(avatarFile, 170, 170),
                () -> verify(resourceService, times(1)).uploadFile(avatarFile, folderName),
                () -> verify(resourceService, times(1)).uploadFile(smallAvatarFile, folderName),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(resourceRepository, times(1)).saveAll(List.of(avatar, smallAvatar)),
                () -> verify(resourceMapper, times(1)).toDto(List.of(avatar, smallAvatar)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(avatar.getId(), returned.get(0).getId()),
                () -> assertEquals(smallAvatar.getId(), returned.get(1).getId())
        );
    }

    @Test
    void get_AvatarExists_ThenReturnedAsInputStream() {
        when(resourceRepository.findById(avatar.getId())).thenReturn(Optional.ofNullable(avatar));
        try {
            when(resourceService.getFile(avatar.getKey())).thenReturn(avatarFile.getInputStream());
        } catch (Exception e) {
            System.out.println("damn");
        }

        userAvatarService.get(avatar.getId());

        assertAll(
                () -> verify(resourceRepository, times(1)).findById(avatar.getId()),
                () -> verify(resourceService, times(1)).getFile(avatar.getKey())
        );
    }

    @Test
    void delete_AvatarDeleted_ThenRandomAvatarIsSetForUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(resourceRepository.findAllByUserId(user.getId())).thenReturn(List.of(avatar, smallAvatar));

        userAvatarService.delete(user.getId());

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(resourceRepository, times(1)).findAllByUserId(user.getId()),
                () -> verify(resourceService, times(1)).deleteFile(avatar.getKey()),
                () -> verify(resourceService, times(1)).deleteFile(smallAvatar.getKey()),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(resourceRepository, times(1)).deleteAll(List.of(avatar, smallAvatar)),
                () -> assertNotEquals(userProfilePic, user.getUserProfilePic())
        );
    }
}
