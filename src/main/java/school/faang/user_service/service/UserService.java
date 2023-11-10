package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.parser.PersonParser;
import school.faang.user_service.pojo.person.Person;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;
import school.faang.user_service.service.contact.ContactService;
import school.faang.user_service.util.PasswordGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ContactService contactService;
    private final ContactPreferenceService contactPreferenceService;
    private final CountryRepository countryRepository;
    private final PersonMapper personMapper;
    private final PersonParser personParser;
    private final ExecutorService parseExecutorService;
    private final PasswordGenerator passwordGenerator;
    @Value("${telegram.botUrl}")
    private String botUrl;
    @Value("${students.partitionSize}")
    private int partitionSize;
    @Value("${students.passwordLength}")
    private int passwordLength;

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<UserDto> users = new ArrayList<>();

        userRepository.findAllById(ids)
                .forEach(user -> users.add(userMapper.toDto(user)));
        log.info("Users have taken from DB successfully");
        return users;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtos = userPage.getContent().stream()
                .map(userMapper::toDto)
                .toList();

        return new PageImpl<>(userDtos);
    }

    @Transactional
    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("This user was not found"));
        log.info("User with id={} has taken successfully from DB", userId);
        return userMapper.toDto(user);
    }

    public String setTelegramContact() {
        long userId = userContext.getUserId();
        return botUrl + userId;
    }

    @Transactional
    public void saveTelegramChatId(long userId, long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        ContactPreferenceDto contactPreference = ContactPreferenceDto.builder()
                .user(user)
                .preference(PreferredContact.TELEGRAM)
                .build();
        contactPreferenceService.save(contactPreference);
        ContactDto contact = ContactDto.builder()
                .user(user)
                .contact(String.valueOf(chatId))
                .type(ContactType.TELEGRAM)
                .build();
        contactService.save(contact);
        log.info("Telegram contact for user with id={} was saved successfully to DB", userId);
    }

    @Transactional
    public void saveStudents(MultipartFile studentsFile) {
        List<Person> students = personParser.parse(studentsFile);

        if (students.size() > partitionSize) {
            ListUtils.partition(students, partitionSize)
                    .forEach(partition -> parseExecutorService.execute(() -> mapAndSaveStudents(partition)));
        } else {
            parseExecutorService.execute(() -> mapAndSaveStudents(students));
        }
    }

    private void mapAndSaveStudents(List<Person> students) {
        List<User> users = students.stream()
                .map(personMapper::toUser)
                .peek(user -> {
                    user.setPassword(passwordGenerator.generateRandomPassword(passwordLength));
                    countryRepository.findByTitle(user.getCountry().getTitle())
                            .ifPresentOrElse(
                                    user::setCountry,
                                    () -> user.setCountry(countryRepository.save(user.getCountry()))
                            );
                })
                .toList();

        log.info("Successfully mapped from Person and saved {} users", users.size());
        userRepository.saveAll(users);
    }
}
