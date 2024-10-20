package school.faang.user_service.service.user;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import school.faang.user_service.dto.projectfollower.ProjectFollowerEvent;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.remote.AmazonS3CustomException;
import school.faang.user_service.exception.remote.ImageGeneratorException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.publisher.follower.ProjectFollowerEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.image.RemoteImageService;
import school.faang.user_service.service.promotion.PromotionManagementService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.util.file.CsvUtil;
import school.faang.user_service.validator.user.UserValidator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final RemoteImageService remoteImageService;
    private final S3Service s3Service;
    private final CsvUtil csvUtil;
    private final CountryService countryService;
    private final List<UserFilter> userFilters;
    private final PromotionManagementService promotionManagementService;
    private final ProjectFollowerEventPublisher projectFollowerEventPublisher;


    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
    }

    public UserDto getUser(long userId) {
        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " does not exist"));

        return userMapper.toDto(existedUser);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserIdIsPositiveAndNotNull);

        return userMapper.toDtos(userRepository.findAllById(ids));
    }

    @Transactional
    public List<UserDto> saveUsersFromCsvFile(MultipartFile multipartFile) {
        List<Person> persons = csvUtil.parseCsvMultipartFile(multipartFile, Person.class);
        List<User> users = userMapper.toEntities(persons);

        users.parallelStream()
                .forEach(user -> {
                    setDefaultPassword(user);

                    Country country = countryService.findCountryAndSaveIfNotExists(user.getCountry().getTitle());

                    user.setCountry(country);
                });
        userRepository.saveAll(users);

        return userMapper.toDtos(users);
    }

    private void setDefaultPassword(User user) {
        user.setPassword(user.getUsername());
    }

    public List<User> getUsersById(List<Long> usersId) {
        return userRepository.findAllById(usersId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public UserDto registerUser(UserRegistrationDto userRegistrationDto) {
        log.info("registerUser() - start : userRegistrationDto = {}", userRegistrationDto);

        User user = userMapper.toEntity(userRegistrationDto);
        userValidator.validateUserConstrains(user);

        setUserDefaultAvatar(user);
        log.info("Trying save new user {}", user);
        userRepository.save(user);

        log.info("registerUser() - end : user = {}", user);
        return userMapper.toDto(user);
    }

    @Transactional
    public List<UserDto> users(UserFilterDto filterDto) {
        promotionManagementService.removeExpiredPromotions();

        List<User> filteredUsers = filteredUsers(filterDto);
        List<User> prioritizedUsers = prioritizedUsers(filteredUsers);

        markAsShownUsers(prioritizedUsers);

        return prioritizedUsers.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private List<User> filteredUsers(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(userRepository::findAll)
                .orElseGet(Collections::emptyList);
    }

    private List<User> prioritizedUsers(List<User> users) {
        return users.stream()
                .sorted(Comparator.comparingInt(this::userPriority).reversed())
                .collect(Collectors.toList());
    }

    private Integer userPriority(User user) {
        return user.getPromotions().stream()
                .filter(promotion -> PromotionTarget.PROFILE.name().equals(promotion.getTarget()))
                .findFirst()
                .map(Promotion::getPriority)
                .orElse(0);
    }

    private void markAsShownUsers(List<User> users) {
        List<Long> promotionIds = users.stream()
                .map(User::getPromotions)
                .filter(promotions -> !promotions.isEmpty())
                .flatMap(promotions -> promotions.stream().filter(
                        promotion -> PromotionTarget.PROFILE.name().equals(promotion.getTarget()))
                )
                .map(Promotion::getId)
                .toList();

        promotionManagementService.markAsShowPromotions(promotionIds);
    }

    private void setUserDefaultAvatar(User user) {
        String key;

        try {
            key = assignUserRemoteRandomPicture(user);
        } catch (Exception e) {
            log.info("Couldn't apply remote picture to user profile. Trying to apply default picture from cloud");

            if (s3Service.isDefaultPictureExistsOnCloud()) {
                log.info("Found default picture on cloud");

                key = s3Service.getDefaultPictureName();
            } else {
                log.info("Couldn't found default picture on cloud");
                return;
            }
        }

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(key)
                .build());

        log.info("Finish assignment random picture for user {}", user);
    }

    private String assignUserRemoteRandomPicture(User user) {
        log.info("Trying to assign random picture for user {}", user);

        try {
            ResponseEntity<byte[]> pictureContent = remoteImageService.getUserProfileImageFromRemoteService();

            String s3Folder = user.getUsername() + user.getId() + "profilePic";
            return s3Service.uploadHttpData(pictureContent, s3Folder);
        } catch (FeignException | AmazonS3CustomException e) {
            log.error(e.getMessage());
            throw new ImageGeneratorException(e.getMessage());
        }
    }

    public void subscribeToProject(Long followerId, Long projectId, Long creatorId) {
        ProjectFollowerEvent event = ProjectFollowerEvent.builder()
                .followerId(followerId)
                .projectId(projectId)
                .creatorId(creatorId)
                .build();

        projectFollowerEventPublisher.publish(event);
    }
}
