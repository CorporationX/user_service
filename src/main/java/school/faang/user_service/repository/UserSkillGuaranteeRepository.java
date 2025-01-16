package school.faang.user_service.repository;

import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    @Query(nativeQuery = true, value = """
            SELECT g.* FROM user_skill_guarantee g
            WHERE user_id = ?1
            """)
    List<UserSkillGuarantee> findAllByUserId(long userId);

    @Modifying
    @Query("DELETE FROM UserSkillGuarantee g" +
            " WHERE g.guarantor.id = :guarantorId")
    void deleteAllByGuarantorId(@Param("guarantorId") Long guarantorId);
}