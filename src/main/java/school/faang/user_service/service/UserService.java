package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow(() ->
                new IllegalArgumentException("The Requester with id =" + id + " does not exist"));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepo.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
