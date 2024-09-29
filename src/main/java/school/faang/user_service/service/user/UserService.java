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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Transactional
    public List<UserDto> saveUsersFromCsvFile(MultipartFile multipartFile) {
        List<UserDto> response = new ArrayList<>();

        CompletableFuture.supplyAsync(() -> csvUtil.parseCsvMultipartFile(multipartFile, Person.class))
                .thenAccept(result -> {
                    List<User> users = userMapper.toEntities(result);
                    users.parallelStream()
                            .forEach(user -> {
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
                    users.forEach(user -> response.add(userMapper.toDto(user)));
                })
                .join();

        return response;
    }

    private void setDefaultPassword(List<User> users) {
        users.forEach(user -> user.setPassword(user.getUsername()));
    }

    public List<User> getUsersById(List<Long> usersId) {
        return userRepository.findAllById(usersId);
    }
}
