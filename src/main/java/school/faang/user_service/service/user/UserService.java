package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import com.json.student.PersonSchemaForUser;
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

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DiceBearService diceBearService;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        User newUser = userRepository.save(user);
        addCreateData(newUser);

        return userMapper.toDto(newUser);
    }

    public List<UserDto> createUserCSV(InputStream inputStream) {
        List<PersonSchemaForUser> persons = parseCsv(inputStream);
        List<UserDto> users = userMapper.toDtoPersons(persons);
        return null;
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
