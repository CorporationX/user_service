package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;

    private final UserRepository repository;

    public UserDto getUser(Long userId){
        if(userId == null) {
            throw new RuntimeException("user id is null");
        }
        Optional<User> user = repository.findById(userId);
        UserDto dto = mapper.toDto(user.orElse(null));
        return dto;
    }

    public List<UserDto> getUsersByIds(List<Long> ids){
        Iterable<User> iterable = repository.findAllById(ids);
        Stream<User> stream = StreamSupport.stream(iterable.spliterator(),false);
        return stream.map(user -> mapper.toDto(user))
                .toList();
    }
}
