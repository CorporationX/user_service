package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;

    private final UserRepository repository;

    UserDto getUser(long userId){
        Optional<User> user = repository.findById(userId);
        UserDto dto = mapper.toDto(user.orElse(null));
        return dto;
    }

    List<UserDto> getUsersByIds(List<Long> ids){
        List<UserDto> dtoList = repository.findAllById(ids);
    }
}
