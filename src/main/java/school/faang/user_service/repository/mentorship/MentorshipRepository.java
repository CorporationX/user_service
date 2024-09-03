package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

import java.lang.annotation.Native;
import java.util.Optional;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

}
