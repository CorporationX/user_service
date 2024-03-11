package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final AvatarService avatarService;

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<UserDto> userDtoStream = userRepository.findPremiumUsers().map(userMapper::toDto);
        return userFilter(userDtoStream, userFilterDto).toList();
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User by id: " + userId + " not found"));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserCreateDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toEntity(userCreateDto);
        user.setActive(true);
        avatarService.generateAndSaveAvatar(user).ifPresent(user::setUserProfilePic);
        return userMapper.toUserCreateDto(userRepository.save(user));
    }

   public boolean existById(long id){
        return userRepository.existsById(id);
   }

    private Stream<UserDto> userFilter(Stream<UserDto> userDtoStream, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userDtoStream, userFilterDto));
    }
}