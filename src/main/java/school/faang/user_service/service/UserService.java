package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserContactsDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.event.ContactPreferenceUpdateEvent;
import school.faang.user_service.event.UserProfileDeactivatedEvent;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserContactsMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.parser.CsvParser;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class UserService {
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonToUserMapper personToUserMapper;
    private final UserContactsMapper userContactsMapper;
    private final CountryService countryService;
    private final EventService eventService;
    private final ContactPreferenceService contactPreferenceService;
    private final CsvParser parser;
    private final List<Filter<User, UserFilterDto>> userFilters;
    private final UserValidator userValidator;
    private final ContactPreferenceRepository contactPreferenceRepository;
    private final MentorshipService mentorshipService;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       PersonToUserMapper personToUserMapper,
                       UserContactsMapper userContactsMapper,
                       UserValidator userValidator,
                       CountryService countryService,
                       @Lazy MentorshipService mentorshipService,
                       @Lazy EventService eventService,
                       ContactPreferenceService contactPreferenceService,
                       List<Filter<User, UserFilterDto>> userFilters,
                       CsvParser parser,
                       ApplicationEventPublisher eventPublisher,
                       ContactPreferenceRepository contactPreferenceRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.personToUserMapper = personToUserMapper;
        this.userContactsMapper = userContactsMapper;
        this.userValidator = userValidator;
        this.countryService = countryService;
        this.mentorshipService = mentorshipService;
        this.eventService = eventService;
        this.contactPreferenceService = contactPreferenceService;
        this.userFilters = userFilters;
        this.parser = parser;
        this.eventPublisher = eventPublisher;
        this.contactPreferenceRepository = contactPreferenceRepository;
    }

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User not found by id: %s", id)));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    public UserDto findUserDtoById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    @Transactional
    public UserDto deactivateProfile(long userId) {
        User user = findUserById(userId);
        stopAllUserActivities(user);
        markUserAsInactive(user);
        userRepository.save(user);
        publishUserProfileDeactivatedEvent(userId);
        return userMapper.toDto(user);
    }

    public ProcessResultDto importUsersFromCsv(InputStream inputStream) throws IOException {
        List<Person> persons = parsePersons(inputStream);
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Person person : persons) {
            if (processPerson(person, errors)) {
                successCount++;
            }
        }

        logProcessingSummary(persons.size(), successCount, errors.size());

        return new ProcessResultDto(successCount, errors);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (user.getBanned()) {
            throw new IllegalArgumentException("User is already banned");
        }
        user.setBanned(true);
        userRepository.save(user);
    }

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        try (Stream<User> premiumUsersStream = userRepository.findPremiumUsers()) {
            Stream<User> filteredStream = applyFilters(premiumUsersStream, filterDto);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public List<UserDto> getAllUsers(UserFilterDto filterDto) {
        try (Stream<User> usersStream = userRepository.findAll().stream()) {
            Stream<User> filteredStream = applyFilters(usersStream, filterDto);

            return filteredStream
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    public UserContactsDto getUserContacts(Long userId) {
        User user = findUserById(userId);
        log.info("Fetched Contact Preference: {}", user.getContactPreference() != null ? user.getContactPreference().getPreference() : "null");

        return userContactsMapper.toDto(user);
    }

    @Transactional
    public UserProfileSettingsResponseDto saveProfileSettings(Long userId, UserProfileSettingsDto userProfileSettingsDto) {
        userValidator.validateUserById(userId);
        ContactPreference contactPreference;

        User user = userRepository.findById(userId).get();
        Optional<ContactPreference> contactPreferenceExisting = contactPreferenceRepository.findByUserId(user.getId());

        if (contactPreferenceExisting.isPresent()) {
            contactPreference = contactPreferenceExisting.get();
            contactPreference.setPreference(userProfileSettingsDto.getPreference());
        } else {
            contactPreference = ContactPreference.builder()
                    .user(user)
                    .preference(userProfileSettingsDto.getPreference())
                    .build();
        }

        ContactPreference savedContactPreference = contactPreferenceRepository.save(contactPreference);

        return new UserProfileSettingsResponseDto(
                savedContactPreference.getId(),
                savedContactPreference.getPreference(),
                userId
        );
    }

    public UserProfileSettingsResponseDto getProfileSettings(Long userId) {
        userValidator.validateUserById(userId);
        userValidator.validateUserProfileByUserId(userId);

        User user = userRepository.findById(userId).get();

        return userMapper.toDto(contactPreferenceRepository.findByUserId(user.getId()).get());
    }

    private List<Person> parsePersons(InputStream inputStream) throws IOException {
        return parser.parseCsv(inputStream);
    }

    private boolean processPerson(Person person, List<String> errors) {
        try {
            User user = createUserFromPerson(person);
            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            String errorMessage = handleDataIntegrityViolation(person, e);
            errors.add(errorMessage);
        } catch (Exception e) {
            logUnexpectedError(person, e, errors);
        }
        return false;
    }

    private void logUnexpectedError(Person person, Exception e, List<String> errors) {
        String errorMessage = String.format("Unexpected error for user '%s %s': %s",
                person.getFirstName(), person.getLastName(), e.getMessage());
        log.error(errorMessage, e);
        errors.add(errorMessage);
    }

    private void logProcessingSummary(int total, int successCount, int errorCount) {
        log.info("Processed {} users. {} succeeded, {} failed.", total, successCount, errorCount);
    }

    private String handleDataIntegrityViolation(Person person, DataIntegrityViolationException e) {
        String constraintName = extractConstraintName(e.getMessage());
        String errorMessage = String.format(
                "Failed to save user '%s %s'. User with this [%s] already exists.",
                person.getFirstName(),
                person.getLastName(),
                constraintName != null ? constraintName : "unknown constraint"
        );

        log.error("Data integrity violation while saving user '{} {}': {}",
                person.getFirstName(), person.getLastName(), e.getMessage(), e);

        return errorMessage;
    }

    private String extractConstraintName(String errorMessage) {
        Pattern pattern = Pattern.compile("constraint \\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void stopAllUserActivities(User user) {
        removeGoals(user);
        removeOwnedEvents(user);
    }

    private void publishUserProfileDeactivatedEvent(Long userId) {
        UserProfileDeactivatedEvent event = new UserProfileDeactivatedEvent(this, userId);
        eventPublisher.publishEvent(event);
        log.info("Published User Profile Deactivated Event for user with id: {}", userId);
    }

    private void markUserAsInactive(User user) {
        user.setActive(false);
    }

    private void removeGoals(User user) {
        user.getSetGoals().removeIf(goal -> goal.getUsers().isEmpty());
    }

    private Stream<User> applyFilters(Stream<User> users, UserFilterDto filterDto) {
        for (Filter<User, UserFilterDto> filter : userFilters) {
            if (filter.isApplicable(filterDto)) {
                users = filter.apply(users, filterDto);
            }
        }
        return users;
    }

    private void removeOwnedEvents(User user) {
        user.getOwnedEvents().removeIf(event -> event.getStatus() == EventStatus.CANCELED);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    private User createUserFromPerson(Person person) {
        String password = generateRandomPassword();
        User user = personToUserMapper.personToUser(person);
        user.setPassword(password);
        Country country = countryService.findOrCreateCountry(
                person.getContactInfo().getAddress().getCountry());
        user.setCountry(country);
        return user;
    }

    @Transactional
    public UserContactsDto updateUserPreferredContact(Long userId, PreferredContact contact, Long currentUserId) {
        userValidator.hasAccess(currentUserId, userId);

        User user = findUserById(userId);

        contactPreferenceService.updatePreference(user, contact);

        eventPublisher.publishEvent(new ContactPreferenceUpdateEvent(userId, contact, currentUserId));

        User updatedUser = findUserById(userId);

        return userContactsMapper.toDto(updatedUser);
    }
}
