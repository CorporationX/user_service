package school.faang.user_service.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.PromotionServiceClient;
import school.faang.user_service.config.kafka.KafkaTopics;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserNotificationDto;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.dto.promotion.UserPromotionRequest;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.MinioSaveException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.exception.UserAlreadyExistsException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.model.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.external.AvatarService;
import school.faang.user_service.service.external.MinioStorageService;
import school.faang.user_service.service.filter.UserFilter;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.util.ConverterUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static school.faang.user_service.config.KafkaConstants.PAYMENT_PROMOTION_TOPIC;
import static school.faang.user_service.config.KafkaConstants.USER_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConverterUtil converterUtil;
    private final PromotionServiceClient promotionServiceClient;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final MinioStorageService minioStorageService;
    private final List<UserFilter> userFilters;
    private final CountryService countryService;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);

        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> goalService.removeUserFromGoal(goal, userId));

        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> neededToRemove = new ArrayList<>();
        user.getOwnedEvents().forEach(event -> {
            if (event.getStartDate().isAfter(currentTime)) { //Если ивент ещё не начался - удаляем
                neededToRemove.add(event);
                eventService.deleteEvent(event.getId()); //Удаление ивентов из БД
            }
        });
        user.setOwnedEvents(user.getOwnedEvents().stream()
                .filter(event -> !neededToRemove.contains(event)).toList()); // Удаление ивентов из списка пользователя


        user.setActive(false);
        userRepository.save(user);

        mentorshipService.removeMentorship(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        List<User> users = userRepository.findPremiumUsers().toList();
        return userFilters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .flatMap(filter -> filter.accept(users.stream(), filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public void userPromotion(UserPromotionRequest userPromotionRequest) {
        findById(userPromotionRequest.userId());
        String message = converterUtil.convertToJson(userPromotionRequest);
        kafkaTemplate.send(PAYMENT_PROMOTION_TOPIC, USER_KEY, message);
    }

    public List<UserDto> getPromotionUsers() {
        List<Long> userIds = promotionServiceClient.getPromotionUsers();
        return userIds.stream()
                .map(userId -> userMapper.toDto(findById(userId)))
                .toList();
    }

    @Transactional
    public UserRegisterResponse register(@Valid UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("username: " + request.username() + " is busy");
        }
        String avatar = avatarService.getRandomAvatar().block();
        String avatarId = UUID.randomUUID().toString();

        try {
            minioStorageService.saveFile(avatar, avatarId);
        } catch (Exception e) {
            throw new MinioSaveException("Minio error save file" + e.getMessage());
        }

        User user = userMapper.toEntity(request);
        user.setRatingPoints(0);

        ContactPreference contactPreference = ContactPreference.builder().user(user).preference(PreferredContact.EMAIL).build();
        if (request.preferredContact() != null) {
            contactPreference.setPreference(request.preferredContact());
        }

        user.setContactPreference(contactPreference);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarId);
        user.setUserProfilePic(userProfilePic);

        userRepository.save(user);

        return userMapper.toUserRegisterResponse(user);
    }

    public byte[] getUserAvatar(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException.userNotFoundException(userId);
        }

        String fileId = userRepository.getUserProfileFileId(userId)
                .orElseThrow(() -> ResourceNotFoundException.userAvatarNotFoundException(userId));


        try {
            String avatar = minioStorageService.getFile(fileId);
            return avatar.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MinioSaveException("Minio error save file" + e.getMessage());
        }
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(userId));
    }

    public UserDto getUser(@Positive @NotNull Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(userId));
    }

    public List<UserDto> getUsersByIds(@NotEmpty List<@Positive Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Long> getFollowersIds(Long authorId) {
        return userRepository.findFollowersByUserId(authorId).stream().map(User::getId).toList();
    }
  
    @Transactional
    public void processCsvFile(MultipartFile csvFile) {
        try {
            InputStream file = csvFile.getInputStream();
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            MappingIterator<Person> mappingIterator = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(file);
            List<Person> people = mappingIterator.readAll();
            List<User> users = people.stream()
                    .filter(person -> !userRepository.existsByPhone(person.getPhone())
                            && !userRepository.existsByEmail(person.getEmail()))
                    .map(this::processPerson)
                    .toList();
            userRepository.saveAll(users);
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка при чтении CSV файла", e);
        }
    }

    private User processPerson(Person person) {
        String username = generateUsername(person);
        String password = generatePassword();
        String personCountry = person.getCountry();
        Country country = countryService.findOrCreateCountry(personCountry);
        String aboutMe = generateAboutMe(person);
        User user = userMapper.toEntity(person, username, password, country, aboutMe);
        if (user.getRatingHistories() == null) {
            user.setRatingHistories(new ArrayList<>());
        }
        return user;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateUsername(Person person) {
        StringBuilder username = new StringBuilder(person.getFirstName() + "." + person.getLastName());
        int id = 0;
        while (userRepository.existsByUsername(username.toString())) {
            username.append(id++);
        }
        return username.toString();
    }

    private String generateAboutMe(Person person) {
        return String.format(
                "State: %s; Faculty: %s; Year of study: %s; Major: %s; Employer: %s",
                person.getState() != null ? person.getState() : "N/A",
                person.getFaculty() != null ? person.getFaculty() : "N/A",
                person.getYearOfStudy() != null ? person.getYearOfStudy() : "N/A",
                person.getMajor() != null ? person.getMajor() : "N/A",
                person.getEmployer() != null ? person.getEmployer() : "N/A"
        );
    }

    public UserNotificationDto getNotificationInfo(@NotNull @Positive Long userId) {
        User user = getUserById(userId);

        return userMapper.toUserNotificationDto(user);
    }

    @Transactional
    public void banUser(Long userId) {
        userRepository.banUserById(userId);
        log.info("User {} has been banned", userId);
    }

    @KafkaListener(topics = KafkaTopics.USER_BAN_TOPIC)
    @Transactional
    public void listenUserBan(String userId) {
        banUser(Long.parseLong(userId));
    }
}
