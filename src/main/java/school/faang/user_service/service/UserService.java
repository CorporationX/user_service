package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mymappers.User1Mapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final User1Mapper mapper;

    public UserDto getUser(long id) {
        User foundUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return mapper.toDto(foundUser);
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        List<User> foundUsers = repository.findAllById(userIds);

        return mapper.toDtos(foundUsers);
    }
}
