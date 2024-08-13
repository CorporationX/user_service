package school.faang.user_service.repository.mentorship;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

  @Query(value = """
        select
            u.*
        from users u
        inner join mentorship m on m.mentee_id = u.id
        where m.mentor_id = :mentor_id and u.active = true
        """, nativeQuery = true)
  List<User> getAllMenteesById(@Param("mentor_id") long id);

  @Query(value = """
        select
            u.*
        from users u
        inner join mentorship m on m.mentor_id = u.id
        where m.mentee_id = :mentee_id and u.active = true
        """, nativeQuery = true)
  List<User> getAllMentorsById(@Param("mentee_id") long id);

  @Query(value = """
        select
            m.id
        from mentorship m
        where m.mentor_id = :mentor_id and m.mentee_id = :mentee_id
        """, nativeQuery = true)
  long getMentorshipIdByMentorIdAndMenteeId(@Param("mentor_id") long mentorId, @Param("mentee_id") long menteeId);

  @Modifying
  @Transactional
  @Query(value = """
        delete
        from mentorship m
        where m.id = :id
        """, nativeQuery = true)
  int deleteMentorshipById(@Param("id") long id);

}
