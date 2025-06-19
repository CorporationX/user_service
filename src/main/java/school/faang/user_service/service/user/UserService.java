package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.dto.user.UsersFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.producer.DataSender;
import school.faang.user_service.kafka.producer.KafkaTopics;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.mapper.analytics.AnalyticsEventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.promotion.utils.ProfilePromotionsViewCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AvatarService avatarService;

    private final DataSender dataSender;
    private final KafkaTopics kafkaTopics;
    private final AnalyticsEventMapper analyticsEventMapper;
    private final ProfilePromotionsViewCalculator viewCalculator;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id %d not found!", id)
                ));
    }

    public UserDto create(String username, String countryTitle, String email, String password) {
        validate(username, countryTitle, email, password);
        Country country = countryRepository.findByTitle(countryTitle)
                .orElseThrow(() -> new DataValidationException("Unknown country: " + countryTitle));
        User userEntity = userMapper.toEntity(username, country, email, passwordEncoder.encode(password));
        User user = userRepository.save(userEntity);
        avatarService.generateRandomAvatar(user.getId());
        return userMapper.toDto(user);
    }

    public void delete(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }

    private void validate(String username, String countryTitle, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new DataValidationException("Username must not be empty");
        }
        if (countryTitle == null || countryTitle.isBlank()) {
            throw new DataValidationException("Country must not be empty");
        }
        if (email == null || email.isBlank()) {
            throw new DataValidationException("Email must not be empty");
        }
        if (password == null || password.isBlank()) {
            throw new DataValidationException("Password must not be empty");
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new DataValidationException("Email is not a valid format");
        }
        if (password.length() < 6) {
            throw new DataValidationException("Password must be at least 6 characters long");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DataValidationException("An account with that email already exists");
        }
    }

    public Slice<UserViewDto> getAllUsers(@NotNull(message = "User filter dto cannot be null")
                                          UsersFilterDto usersFilterDto,
                                          @NotNull(message = "User id cannot be null")
                                          Long userId) {
        Map<Plan, Integer> planIntegerMap = viewCalculator.calculatePromotedViews(usersFilterDto.getSize());
        Slice<User> userVipPromotion = userRepository.findAllActivePromotionByPlan(Plan.VIP,
                PageRequest.of(usersFilterDto.getPage(), planIntegerMap.get(Plan.VIP)));
        Slice<User> usersGoldPromotion = userRepository.findAllActivePromotionByPlan(Plan.GOLD,
                PageRequest.of(usersFilterDto.getPage(), planIntegerMap.get(Plan.GOLD)));
        Slice<User> usersPlusPromotion = userRepository.findAllActivePromotionByPlan(Plan.PLUS,
                PageRequest.of(usersFilterDto.getPage(), planIntegerMap.get(Plan.PLUS)));
        Slice<User> usersNoPromotion = userRepository.findAllWithoutPromotion(PageRequest.of(usersFilterDto.getPage(),
                planIntegerMap.get(null)));

        List<User> promotedUsers = new ArrayList<>();
        userVipPromotion.forEach(promotedUsers::add);
        usersGoldPromotion.forEach(promotedUsers::add);
        usersPlusPromotion.forEach(promotedUsers::add);

        List<User> allUsers = new ArrayList<>(promotedUsers);
        usersNoPromotion.forEach(allUsers::add);

        sendPromotedUsersAnalytics(promotedUsers, userId);
        sendAllUsersAnalytics(allUsers, userId);

        return mergeSlices(PageRequest.of(usersFilterDto.getPage(), usersFilterDto.getSize()),
                List.of(userVipPromotion, usersGoldPromotion, usersPlusPromotion, usersNoPromotion));
    }

    private Slice<UserViewDto> mergeSlices(PageRequest pageRequest, List<Slice<User>> slices) {
        List<UserViewDto> mergedUsers = new ArrayList<>();
        boolean hasNext = false;
        for (Slice<User> slice : slices) {
            mergedUsers.addAll(userMapper.toUserViewDtos(slice.getContent()));
            if (slice.hasNext()) {
                hasNext = true;
            }
        }
        return new SliceImpl<>(mergedUsers, pageRequest, hasNext);
    }

    private void sendAllUsersAnalytics(List<User> allUsers, @NotNull(message = "User id cannot be null") Long userId) {
        for (User user : allUsers) {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.fromUser(user, userId);
            dataSender.send(kafkaTopics.getAnalyticsCreatedTopic(), analyticsEvent);
            log.info("Send analytics profile event to kafka topic");
        }
    }

    private void sendPromotedUsersAnalytics(List<User> promotedUsers, @NotNull(message = "User id cannot be null") Long userId) {
        for (User user : promotedUsers) {
            AnalyticsEvent analyticsEvent = analyticsEventMapper.fromUser(user, userId);
            dataSender.send(kafkaTopics.getAnalyticsProfileEventTopic(), analyticsEvent);
            log.info("Send analytics profile event to kafka topic");
        }
    }
}