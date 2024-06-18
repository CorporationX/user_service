package school.faang.user_service.service.user.parse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DataFromFileService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    private Parser parser;

    private ToCsvListConverter toCsvListConverter;

    private ToInputStreamListConverter toInputStreamListConverter;

    private ToUserListConverter toUserListConverter;

    public List<UserDto> saveUsersFromFile(InputStream inputStream) {
        List<CSVPart> csvParts = toCsvListConverter.convertToCsvList(inputStream);
        List<InputStream> inputStreamParts = toInputStreamListConverter.convertToInputStreamList(csvParts);
        List<Person> allPersons = parser.multiParser(inputStreamParts);
        List<User> allUsers = toUserListConverter.convertToUserList(allPersons);

        List<User> updatedUsers = new ArrayList<>(allUsers);
        Iterable<User> iterableForExistingUsers = userRepository.findAll();

        for (User existingUser : iterableForExistingUsers) {
            for (User user : allUsers) {
                updatedUsers.removeIf(u ->
                        existingUser.getUsername().equals(user.getUsername())
                                || existingUser.getEmail().equals(user.getEmail())
                                || existingUser.getPhone().equals(user.getPhone()));
            }
        }
        userRepository.saveAll(updatedUsers);
        return userMapper.toDto(updatedUsers);
    }
}
