package school.faang.user_service.repository;

import org.springframework.data.repository.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@org.springframework.stereotype.Repository
public interface UserCheckRepository extends Repository<User, Long> {
    List<User> findDistinctPeopleByUsernameOrEmailOrPhone(String username, String email, String phone);
}
