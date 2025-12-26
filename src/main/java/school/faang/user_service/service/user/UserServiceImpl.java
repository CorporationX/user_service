package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.StudentCsvRowMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.csvmapper.StudentCsvRow;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.utils.PasswordUtils;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final StudentCsvRowMapper studentCsvRowMapper = Mappers.getMapper(StudentCsvRowMapper.class);

    @Override
    public UserDto create(CreateUserDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        User user = userMapper.toUser(userDto);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toUserDtoList(users);
    }

    @Override
    public List<UserDto> addStudents(MultipartFile file) throws IOException {
        List<UserDto> userDtos = new ArrayList<>();

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try (
                InputStream input = file.getInputStream();
                Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
        ) {
            MappingIterator<StudentCsvRow> it =
                    csvMapper.readerFor(StudentCsvRow.class).with(schema).readValues(reader);

            while (it.hasNext()) {
                StudentCsvRow row = it.next();

                Person person = studentCsvRowMapper.toPerson(row);

                User user = userMapper.personToUser(person);
                String password = PasswordUtils.generatePassword(minPasswordLength);
                user.setPassword(password);

                String countryName = row.getCountry();
                Country country = countryRepository.findByTitle(countryName)
                        .orElseGet(() -> {
                            Country c = new Country();
                            c.setTitle(countryName);
                            return countryRepository.save(c);
                        });
                user.setCountry(country);
                userRepository.save(user);

                UserDto userDto = convertToUserDto(person);
                userDtos.add(userDto);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Ошибка при чтении CSV файла", e);
        }

        return userDtos;
    }

    private UserDto convertToUserDto(Person person) {
        return new UserDto(
                null,
                person.getFirstName() + " " + person.getLastName(),
                person.getContactInfo().getEmail(),
                person.getContactInfo().getPhone(),
                "About " + person.getFirstName()
        );
    }
}
