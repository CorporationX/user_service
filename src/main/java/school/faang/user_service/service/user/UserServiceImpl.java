package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.promotion.PromoUserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.UserProfileViewEvent;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.pojo.Person;
import school.faang.user_service.publisher.userprofileview.UserProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CountryService countryService;
    private final CsvMapper csvMapper;
    private final ExecutorService executorService;
    private final UserProfileViewEventPublisher userProfileViewEventPublisher;
    private final UserContext userContext;

    private final Random random = new Random();

    @Override
    public User findByIdOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not exists! id: " + userId));
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        User user = findByIdOrThrow(userId);
        UserResponseDto userDto = userMapper.toUserResponseDto(user);
        long visitorId = userContext.getUserId();
        if (visitorId > 0) {
            userProfileViewEventPublisher.publish(new UserProfileViewEvent(userId, visitorId, LocalDateTime.now()));
        }
        log.info("Profile of user {} was viewed", userDto.getUsername());
        return userDto;
    }

    @Override
    public List<PromoUserDto> getUsersByIds(List<Long> ids) {
        final List<User> users = userRepository.findAllById(ids);
        return users.stream().map(userMapper::toPromoUserDto).toList();
    }

    @Override
    public void processPersonsFromFile(MultipartFile file) {
        try (InputStream fileInputStream = file.getInputStream()) {
            log.debug("Begin parsing file");
            List<Person> persons = parsePersonsFromInputStream(fileInputStream);
            log.debug("End parsing file. Read {} records", persons.size());

            log.debug("Begin transforming records to entities");
            List<User> users = mapToUsers(persons);
            log.debug("End transforming records to entities");

            userRepository.saveAll(users);
            log.debug("Entities saved into DB");
            log.info("Persons from file {} are imported", file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error in file {}", file, e);
            throw new RuntimeException("Failed to reading file", e);
        }
    }

    private List<User> mapToUsers(List<Person> persons) {
        return persons.stream()
                .map(person -> CompletableFuture.supplyAsync(() -> {
                    User user = userMapper.toUserEntity(person);
                    user.setPassword(generatePassword());
                    user.setCountry(countryService.getOrCreateCountry(person.getCountry()));
                    return user;
                }, executorService).exceptionally(error -> {
                    log.error("Error processing file", error);
                    throw new RuntimeException("Failed to process users", error);
                })).map(CompletableFuture::join).toList();
    }

    private List<Person> parsePersonsFromInputStream(InputStream fileInputStream) {
        List<Person> persons;
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try {
            MappingIterator<Person> personIterator = csvMapper.readerFor(Person.class)
                    .with(schema)
                    .readValues(fileInputStream);
            persons = personIterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return persons;
    }

    private String generatePassword() {
        return random
                .ints(10, 33, 122)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
