package school.faang.user_service.service.impl;

import com.json.student.Address;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.model.dto.ContactInfoDto;
import school.faang.user_service.model.dto.EducationDto;
import school.faang.user_service.model.dto.PersonDto;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.event.ProfileViewEvent;
import school.faang.user_service.model.event.SearchAppearanceEvent;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.UserProfilePic;
import school.faang.user_service.model.entity.Event;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.model.entity.TelegramContact;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.TelegramContactRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EventRepository;
import school.faang.user_service.repository.GoalRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.S3service;
import school.faang.user_service.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final static String PROMOTION_TARGET = "profile";

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final PromotionRepository promotionRepository;
    private final List<UserFilter> userFilters;
    private final PersonToUserMapper personToUserMapper;
    private final CountryService countryService;
    private final S3service s3service;
    private final TelegramContactRepository telegramContactRepository;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    private final ProfileViewEventPublisher profileViewEventPublisher;
    private final UserContext userContext;

    @Override
    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(premiumUsers,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto deactivateUser(UserDto userDto) {
        log.info("Деактивация пользователя с ID: {}", userDto.getId());
        User user = userRepository.findById(userDto.getId()).orElseThrow(
                () -> new NoSuchElementException("Пользователь с ID " + userDto.getId() + " не найден"));
        stopUserActivities(user);
        user.setActive(false);
        mentorshipService.stopMentorship(user);
        userRepository.save(user);
        log.info("Пользователь с ID: {} был успешно деактивирован", userDto.getId());
        return userMapper.toDto(user);
    }

    @Override
    public User stopUserActivities(User user) {
        log.info("Останавливаем активности для пользователя с ID: {}", user.getId());
        stopGoals(user);
        stopEvents(user);
        log.info("Активности пользователя с ID: {} остановлены", user.getId());
        return user;
    }

    @Override
    public User stopGoals(User user) {
        List<Goal> goals = user.getGoals();
        log.info("Останавливаем цели для пользователя с ID: {}. Количество целей: {}", user.getId(), goals.size());
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1 && goal.getUsers().contains(user)) {
                log.info("Удаляем цель с ID: {} для пользователя с ID: {}", goal.getId(), user.getId());
                goalRepository.deleteById(goal.getId());
            }
        }
        user.setGoals(new ArrayList<>());
        log.info("Цели пользователя с ID: {} были очищены", user.getId());
        return user;
    }

    @Override
    public User stopEvents(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();
        List<Long> eventsIdList = new ArrayList<>();
        log.info("Останавливаем события для пользователя с ID: {}. Количество собственных событий: {}", user.getId(), ownedEvents.size());
        if (ownedEvents != null || !ownedEvents.isEmpty()) {
            for (Event event : ownedEvents) {
                eventsIdList.add(event.getId());
                log.info("Удаляем событие с ID: {} для пользователя с ID: {}", event.getId(), user.getId());
            }
            eventRepository.deleteAllById(eventsIdList);
        }
        user.setOwnedEvents(new ArrayList<>());

        List<Event> participatedEvents = user.getParticipatedEvents();
        log.info("Останавливаем участие в событиях для пользователя с ID: {}. Количество участвующих событий: {}", user.getId(), participatedEvents.size());
        if (participatedEvents != null || !participatedEvents.isEmpty()) {
            for (Event event : participatedEvents) {
                List<User> attendees = new ArrayList<>(event.getAttendees());
                attendees.remove(user);
                event.setAttendees(attendees);
                log.info("Удаляем пользователя с ID: {} из участников события с ID: {}", user.getId(), event.getId());
            }
            user.setParticipatedEvents(new ArrayList<>());
        }
        log.info("Участие пользователя с ID: {} в событиях было очищено", user.getId());
        return user;
    }

    @Override
    public UserDto getUser(long userId) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {

        final List<User> users = userRepository.findAllById(ids);

        return userMapper.toListUserDto(users);
    }

    @Override
    @Transactional
    public List<UserDto> getFilteredUsers(UserFilterDto filterDto, Long callingUserId) {
        User callingUser = userRepository.findById(callingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> filteredUsers = getFilteredUsersFromRepository(filterDto);
        List<User> priorityFilteredUsers = getPriorityFilteredUsers(filteredUsers, callingUser);

        if (!filteredUsers.isEmpty()) {
            filteredUsers.stream()
                    .map(user -> new SearchAppearanceEvent(user.getId(), callingUserId, LocalDateTime.now()))
                    .forEach(searchAppearanceEventPublisher::publish);
        }

        decrementRemainingShows(priorityFilteredUsers);
        deleteExpiredProfilePromotions();

        return priorityFilteredUsers.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void saveAvatar(long userId, MultipartFile file) {
        UserProfilePic profilePic = userRepository.findUserProfilePicByUserId(userId);
        if (profilePic != null) {
            s3service.deleteFile(profilePic.getFileId());
            s3service.deleteFile(profilePic.getSmallFileId());
            userRepository.deleteUserProfilePicByUserId(userId);
        }

        UserProfilePic userProfilePic = s3service.uploadFile(file, userId);
        userRepository.saveUserProfilePic(userId, userProfilePic);
    }

    @Override
    public byte[] getAvatar(long userId) {
        UserProfilePic profilePic = userRepository.findUserProfilePicByUserId(userId);
        if (profilePic == null) {
            throw new EntityNotFoundException("User avatar not found");
        }

        String largeFileId = profilePic.getFileId();
        try (InputStream inputStream = s3service.downloadFile(largeFileId)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error downloading file from S3", e);
        }
    }

    @Override
    @Transactional
    public void deleteAvatar(long userId) {
        UserProfilePic profilePic = userRepository.findUserProfilePicByUserId(userId);
        if (profilePic == null) {
            throw new EntityNotFoundException("User avatar not found");
        }

        s3service.deleteFile(profilePic.getFileId());
        s3service.deleteFile(profilePic.getSmallFileId());
        userRepository.deleteUserProfilePicByUserId(userId);
    }

    @Override
    public Long getMaxUserId() {
        return userRepository.findMaxUserId();
    }

    @Override
    @Transactional
    public void updateTelegramUserId(String telegramUserName, String telegramUserId) {
        TelegramContact telegramContact = telegramContactRepository.findByTelegramUserName(telegramUserName).orElseThrow(() ->
                new EntityNotFoundException(String.format("Telegram user with username %s not found", telegramUserName)));

        if (telegramContact.getTelegramUserId() == null) {
            telegramContact.setTelegramUserId(telegramUserId);
            telegramContactRepository.save(telegramContact);
        }
    }

    private List<User> getFilteredUsersFromRepository(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> userRepository.findAll((Specification<User>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<User> getPriorityFilteredUsers(List<User> filteredUsers, User callingUser) {
        return filteredUsers.stream()
                .sorted((Comparator
                        .comparing((User user) -> calculateCountryPriority(user, callingUser))
                        .thenComparing(this::calculatePriorityLevel)))
                .toList();
    }

    private void decrementRemainingShows(List<User> priorityFilteredUsers) {
        List<Long> promotionIds = priorityFilteredUsers.stream()
                .flatMap(user -> {
                    List<Promotion> promotions = user.getPromotions();
                    if (promotions == null) {
                        return Stream.empty();
                    }
                    return promotions.stream()
                            .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()) &&
                                    promotion.getRemainingShows() > 0)
                            .map(Promotion::getId);
                })
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, PROMOTION_TARGET);
        }
    }

    private void deleteExpiredProfilePromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(PROMOTION_TARGET);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }

    private Promotion getTargetPromotion(User user) {
        return user.getPromotions().stream()
                .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()))
                .findFirst()
                .orElse(null);
    }

    private int calculateCountryPriority(User user, User callingUser) {
        if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
            return 1;
        }

        Promotion targetPromotion = getTargetPromotion(user);

        if (targetPromotion != null &&
                targetPromotion.getPriorityLevel() == 3 &&
                !user.getCountry().equals(callingUser.getCountry())) {
            return 1;
        }

        if (targetPromotion == null) {
            return 1;
        }

        return 0;
    }

    private int calculatePriorityLevel(User user) {
        if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
            return 0;
        }

        Promotion targetPromotion = getTargetPromotion(user);

        return targetPromotion != null ? -targetPromotion.getPriorityLevel() : 0;
    }

    @Override
    @Transactional
    public void processCsvFile(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 21) continue;

                Address address = new Address();
                address.setStreet(fields[7].trim());
                address.setCity(fields[8].trim());
                address.setState(fields[9].trim());
                address.setCountry(fields[10].trim());
                address.setPostalCode(fields[11].trim());

                ContactInfoDto contactInfoDto = new ContactInfoDto(fields[5].trim(), fields[6].trim(), address);

                EducationDto educationDto = new EducationDto(
                        fields[12].trim(),  // faculty
                        Integer.parseInt(fields[13].trim()),
                        fields[14].trim(),  // major
                        Double.parseDouble(fields[15].trim())
                );

                PersonDto personDto = new PersonDto(
                        fields[0].trim(),
                        fields[1].trim(),
                        contactInfoDto,
                        educationDto,
                        null,
                        Integer.parseInt(fields[2].trim()),
                        fields[3].trim(),
                        fields[4].trim()
                );

                String countryName = contactInfoDto.address().getCountry();
                Country country = countryService.findOrCreateCountry(countryName);

                User user = personToUserMapper.personToUser(personDto);
                user.setPassword(generatePassword());
                user.setCountry(country);
                user.setAboutMe(generateAboutMe(personDto));

                userRepository.save(user);
            }
        } catch (IOException e) {
            log.error("Ошибка при обработке CSV файла: {}", e.getMessage());
        }
    }

    private String generateAboutMe(PersonDto personDto) {
        StringBuilder aboutMe = new StringBuilder();

        if (personDto.contactInfo() != null && personDto.contactInfo().address() != null) {
            String state = personDto.contactInfo().address().getState();
            if (state != null && !state.isEmpty()) {
                aboutMe.append(state).append(", ");
            }
        }

        if (personDto.education() != null) {
            aboutMe.append(personDto.education().faculty()).append(", ")
                    .append(personDto.education().yearOfStudy()).append(", ")
                    .append(personDto.education().major()).append(", ");
        }

        if (personDto.employer() != null && !personDto.employer().isEmpty()) {
            aboutMe.append(personDto.employer());
        }

        return aboutMe.toString().trim();
    }

    private String generatePassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    @Override
    public void publishProfileViewEvent(long viewerId, long profileOwnerId) {
        if (viewerId != profileOwnerId) {
            ProfileViewEvent event = new ProfileViewEvent(profileOwnerId, viewerId, LocalDateTime.now());
            profileViewEventPublisher.publish(event);

            log.info("Published ProfileViewEvent: viewerId={}, profileOwnerId={}", viewerId, profileOwnerId);
        } else {
            log.debug("Viewer ID is the same as Profile Owner ID. No event published. viewerId={}, profileOwnerId={}",
                    viewerId, profileOwnerId);
        }
    }
}
