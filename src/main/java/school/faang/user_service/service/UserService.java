package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;


    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", id)));
    }

    public boolean isOwnerExistById(Long id) {
        return userRepository.existsById(id);
    }

    public void saveUser(User savedUser) {
        if (isOwnerExistById(savedUser.getId())) {
            userRepository.save(savedUser);
        }
    }

    public List<User> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        Stream<User> userStream = userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .flatMap(filter -> filter.apply(premiumUsers, userFilterDto));
        return userStream.collect(Collectors.toList());
    }
}
