package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.pojo.student.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.util.file.CsvUtil;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final CsvUtil csvUtil;
    private final CountryService countryService;

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

    public List<UserDto> saveUsersFromCsvFile(MultipartFile multipartFile) {

        List<Person> persons = csvUtil.parseCsvToPojo(multipartFile, Person.class);

        List<User> users = userMapper.toEntities(persons);

        users.forEach(user -> {
            setDefaultPassword(users);

            try {
                Country country = countryService.findCountryByTitle(user.getCountry().getTitle());
                user.setCountry(country);
            } catch (EntityNotFoundException e) {
                Country country = countryService.saveCountry(user.getCountry());
                user.setCountry(country);
            }
        });

        userRepository.saveAll(users);

        return userMapper.toDtos(users);
    }

    private void setDefaultPassword(List<User> users) {
        users.forEach(u -> u.setPassword(u.getUsername()));
    }
    public List<User> getUsersById(List<Long> usersId) {
        return userRepository.findAllById(usersId);
    }
}
