package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.service.util.AvatarApiService;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final AvatarApiService avatarApiService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filterDto), (stream, filter) -> stream)
                .toList();
    }

    @Transactional
    public User registerNewUser(User newUser) {
        validateUsername(newUser.getUsername());
        validateEmail(newUser.getEmail());
        validatePhone(newUser.getPhone());
        validateCountry(newUser.getCountry().getId());

        User withId = userRepository.save(newUser);
        Optional<byte[]> optionalAvatarData = avatarApiService.getDefaultAvatar(newUser.getUsername());
        if (optionalAvatarData.isEmpty()) {
            throw new RuntimeException("Can't get response from the avatar API.");
        }
        ObjectMetadata metadata = prepareFileMetadata("image/svg+xml", optionalAvatarData.get().length);
        uploadDefaultAvatar(withId, optionalAvatarData.get(), metadata);

        return userRepository.save(newUser);
    }

    private void uploadDefaultAvatar(User toUpdate, byte[] fileBytes, ObjectMetadata metadata) {
        String folder = String.format("user_%d/profile", toUpdate.getId());
        String fileKey = s3Service.uploadFile(new ByteArrayInputStream(fileBytes), metadata, folder, toUpdate.getId());
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(fileKey);
        profilePic.setSmallFileId(fileKey);
        toUpdate.setUserProfilePic(profilePic);
        log.info("Registered user(userId={}), with default avatar(fileKey={})", toUpdate.getId(), fileKey);
    }

    private ObjectMetadata prepareFileMetadata(String contentType, long sizeInBytes) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(sizeInBytes);
        return metadata;
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException(String.format("Username %s is already in use", username));
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(String.format("Email %s is already in use", email));
        }
    }

    private void validatePhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalStateException(String.format("Phone %s is already in use", phone));
        }
    }

    private void validateCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new IllegalStateException(String.format("Country with ID %d does not exist", countryId));
        }
    }
}
