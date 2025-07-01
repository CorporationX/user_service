package school.faang.user_service.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.country.Country;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.model.user.UserFilter;
import school.faang.user_service.repository.user.UserFilterRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.resource.image.ImageService;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final CountryService countryService;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final UserFilterRepository userFilterRepository;
    private final UserRedisService userRedisService;
    private final PromotionRedisService promotionRedisService;
    private final Executor executor;

    public UserService(UserRepository userRepository,
                       UserContext userContext,
                       UserValidator userValidator,
                       CountryService countryService,
                       PasswordEncoder passwordEncoder,
                       ImageService imageService,
                       UserFilterRepository userFilterRepository,
                       UserRedisService userRedisService,
                       PromotionRedisService promotionRedisService,
                       @Qualifier("getUserExecutor") Executor executor) {
        this.userRepository = userRepository;
        this.userContext = userContext;
        this.userValidator = userValidator;
        this.countryService = countryService;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
        this.userFilterRepository = userFilterRepository;
        this.userRedisService = userRedisService;
        this.promotionRedisService = promotionRedisService;
        this.executor = executor;
    }

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found");
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        long userId = userContext.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User from context not found");
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    @Transactional(readOnly = true)
    public boolean existsByIdOrThrow(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return true;
    }

    @Transactional
    public User registrationUser(User user, Long countryId) {
        userValidator.validateUser(user);

        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        Country country = countryService.getCountryById(countryId);
        user.setCountry(country);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User {} has been saved", savedUser);

        return savedUser;
    }

    @Async(value = "generateRandomAvatarUserExecutor")
    public void createAvatarUser(long userId) {
        User user = getUserByIdOrThrow(userId);

        Resource file = imageService.generateRandomUserAvatar(userId);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFile(file);
        user.setUserProfilePic(userProfilePic);

        User savedUsed = userRepository.save(user);
        log.info("User {} avatar {} has been saved", savedUsed.getId(), user.getUserProfilePic().getSmallFile());
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByFilter(UserFilter filter) {
        List<Long> filteredUserIds = userFilterRepository.findByFilter(filter);

        List<CompletableFuture<User>> futureUsers = filteredUserIds.stream()
            .map(userId -> CompletableFuture.supplyAsync(() ->
                    userRedisService.getUserFromRedisById(userId)
                            .orElseGet(() -> {
                                User user = getUserByIdOrThrow(userId);
                                userRedisService.addUserInRedis(user);
                                return user;
                            }), executor))
            .toList();

        List<User> users = futureUsers.stream()
                .map(CompletableFuture::join)
                .toList();

        promotionRedisService.decrementCountViewByUserIds(filteredUserIds);

        return users;
    }
}
