package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    @Query(nativeQuery = true, value = "INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id) VALUES (?1, ?2, ?3) returning id")
    Long addSkillGuarantee(long userId, long skillId, long guarantorId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) > 0 FROM user_skill_guarantee WHERE user_id = ?1 AND skill_id = ?2 AND guarantor_id = ?3")
    boolean existsGuarantorForUserAndSkill(long userId, long skillId, long guarantorId);
}