package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CsvToPersonParser csvToPersonParser;

    public User getExistingUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found in database"));
    }

    public List<UserDto> generateUsersFromCsv(MultipartFile csvFile) throws IOException {
        List<User> savedUsers = csvToPersonParser.parseUsers(csvFile);
        userRepository.saveAll(savedUsers);
        return userMapper.listToDto(savedUsers);
    }
}