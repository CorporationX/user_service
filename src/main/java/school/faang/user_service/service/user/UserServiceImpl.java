package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.person.ContactInfo;
import school.faang.user_service.entity.person.Education;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.person.PreviousEducation;
import school.faang.user_service.entity.person.Address;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.utils.PasswordUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);

        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                Person person = new Person();
                person.setFirstName(values[0].trim());
                person.setLastName(values[1].trim());
                person.setYearOfBirth(Integer.parseInt(values[2].trim()));
                person.setGroup(values[3].trim());
                person.setStudentId(values[4].trim());

                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setEmail(values[5].trim());
                contactInfo.setPhone(values[6].trim());

                Address address = new Address(values[7].trim(), values[8].trim(),
                        values[9].trim(), values[10].trim(), values[11].trim());
                contactInfo.setAddress(address);
                person.setContactInfo(contactInfo);

                Education education = new Education(
                        values[12].trim(),
                        Integer.parseInt(values[13].trim()),
                        values[14].trim(),
                        Double.parseDouble(values[15].trim())
                );

                List<Education> educations = new ArrayList<>();
                educations.add(education);

                person.setEducations(educations);


                person.setStatus(values[16].trim());
                person.setAdmissionDate(values[17].trim());
                person.setGraduationDate(values[18].trim());

                List<PreviousEducation> previousEducation = new ArrayList<>();
                previousEducation.add(new PreviousEducation(values[19].trim(),
                        values[20].trim(), Integer.parseInt(values[21].trim())));
                person.setPreviousEducation(previousEducation);

                person.setScholarship(Boolean.parseBoolean(values[22].trim()));
                person.setEmployer(values[23].trim());

                User user = userMapper.personToUser(person);

                String password = PasswordUtils.generatePassword(minPasswordLength);
                user.setPassword(password);

                String countryName = person.getContactInfo().getAddress().getCountry();
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
