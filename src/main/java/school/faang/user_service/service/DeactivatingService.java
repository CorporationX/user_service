package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.DtoDeactiv;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeactivatingService {

    private final UserRepository userRepository;

    public DtoDeactiv deactivatingTheUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new DataValidException("there is no user");
        }


        return new DtoDeactiv("the user is deactivated");
    }

}
