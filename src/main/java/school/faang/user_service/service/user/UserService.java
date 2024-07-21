package school.faang.user_service.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private final UserMapper mapper;

    private final UserRepository repository;

    public UserService(UserMapper mapper, UserRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    public UserDto getUser(Long userId){
        if(userId == null) {
            throw new RuntimeException("user id is null");
        }
        Optional<User> user = repository.findById(userId);
        UserDto dto = mapper.toDto(user.orElse(null));
        return dto;
    }

    public List<UserDto> getUsersByIds(List<Long> ids){
        Assert.notNull(ids , "is cannot null");
        List<Optional<User>> result = new ArrayList<>();
        ids.forEach((id) -> {
            Optional<User> user = repository.findById(id);
            Objects.requireNonNull(result);
            result.add(user);
        });
        List<UserDto> dtoList = result.stream()
                .map(user -> mapper.toDto(user))
                .toList();

        return dtoList;
    }
}
