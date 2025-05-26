package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;

    public User getUserById(Long id){
        return userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException
                ("The Requester with id =" + id + " does not exist"));
    }
}
