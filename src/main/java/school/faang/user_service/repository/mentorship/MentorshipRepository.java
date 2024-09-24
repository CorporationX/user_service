package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query(
            nativeQuery = true,
            value = """
                    DELETE FROM mentorship m 
                    WHERE m.mentee_id = :menteeId 
                        AND m.mentor_id = :mentorId
                    """
    )
    void delete(Long menteeId, Long mentorId);
}
