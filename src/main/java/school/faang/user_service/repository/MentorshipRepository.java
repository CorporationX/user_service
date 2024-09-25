package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM mentorship
            WHERE mentorship.mentor_id = :mentorId
            """)
    void deleteByMentorId(Long mentorId);
}
