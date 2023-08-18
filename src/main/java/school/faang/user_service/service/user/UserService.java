package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.PersonSchemaForUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.diceBear.DiceBearService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DiceBearService diceBearService;
    private final int lenPassword = 4;

    public UserDto createUser(UserDto userDto) {

        User user = userMapper.toEntity(userDto);

        synchronized (userRepository) {
            user = userRepository.save(user);
        }

        addCreateData(user);

        return userMapper.toDto(user);
    }

    public List<UserDto> createUserCSV(InputStream inputStream) {
        List<PersonSchemaForUser> persons = parseCsv(inputStream);

        List<CompletableFuture<UserDto>> futures = persons.stream()
                .map(person -> CompletableFuture.supplyAsync(() -> {
                    UserDto userDto = userMapper.personToUserDto(person);
                    userDto.setPassword(generatePassword(lenPassword));

                    validateUserDto(userDto);
                    createUser(userDto);
                    //SMS to user.getEmail with - "You password for CorporationX is:"+user.getPassword()
                    return userDto;
                }))
                .toList();

        var res = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(dto -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join();

        return res;

    }

    public void validateUserDto(@Valid UserDto userDto) {
        System.out.println(userDto);
    }

    private String generatePassword(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0");
        }

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            password.append(digit);
        }

        return password.toString();
    }

    private List<PersonSchemaForUser> parseCsv(InputStream inputStream) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<PersonSchemaForUser> iterator = null;
        try {
            iterator = csvMapper.readerFor(PersonSchemaForUser.class).with(schema).readValues(inputStream);
            return iterator.readAll();
        } catch (IOException e) {
            throw new FileException("Can't read file: " + e.getMessage());
        }
    }

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User was not found"));
    }

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }

    private void addCreateData(User user) {
        user.setCreatedAt(LocalDateTime.now());
        UserProfilePic userProfilePic = diceBearService.createAvatar(user.getUsername(), user.getId());
        user.setUserProfilePic(userProfilePic);
    }
}
