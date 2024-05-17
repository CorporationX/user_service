package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "delete from mentorship where mentor_id = :mentorId")
    void deleteAllMentorshipByMentorId(long mentorId);
}
