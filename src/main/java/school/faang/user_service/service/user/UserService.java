package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.promotion.user.UserToPromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user.UserToPromotionMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static school.faang.user_service.messages.ErrorMessages.USER_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserToPromotionMapper userToPromotionMapper;

    public boolean isExists(long userId) {
        return userRepository.existsById(userId);
    }

    public List<UserToPromotionDto> findAll() {
        return userToPromotionMapper.toDtoList(userRepository.findAll());
    }

    public UserDto getUser(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            String message = USER_NOT_FOUND_ERROR.formatted(userId);
            log.error(message);
            throw new UserNotFoundException(message);
        }
        return userMapper.toDto(userOptional.get());
    }
}
