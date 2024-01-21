package school.faang.user_service.service.user;


import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final List<UserFilter> userFilters;


    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<UserDto> userDtoStream = userRepository.findPremiumUsers().map(user -> userMapper.toUserDto(user));
        return userFilter(userDtoStream, userFilterDto).toList();
    }

    private Stream<UserDto> userFilter(Stream<UserDto> userDtoStream, UserFilterDto userFilterDto) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userDtoStream, userFilterDto));
    }


}
