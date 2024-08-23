package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAllUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .toList();
    }

    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new DataValidationException(String.format("Not found user with id: %d", id)));

        long preferredContactId = userRepository.getPreferredContact(id).orElse(0L);
        PreferredContact contact = PreferredContact.fromOrdinal(preferredContactId);

        UserDto userDto = userMapper.toDto(user);
        userDto.setPreference(contact);

        return userDto;
    }
}
