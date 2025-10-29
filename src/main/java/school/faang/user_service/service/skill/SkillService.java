package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

/**
 * User skills management service:
 * create a skill, get user skills, view offered skills, and acquire a skill from offers.
 */
public interface SkillService {

    /**
     * Creates a new skill.
     *
     * <p>Checks uniqueness by {@code title}; throws if a skill already exists.</p>
     *
     * @param skillDto data to create a skill (title, etc.)
     * @return created skill DTO
     * @throws ForbiddenException if a skill with the same title already exists
     */
    SkillDto create(CreateSkillDto skillDto);

    /**
     * Returns all skills of a user.
     *
     * @param userId user ID
     * @return list of user skills (possibly empty)
     */
    List<SkillDto> getByUserId(Long userId);

    /**
     * Returns skills offered to the user with the number of offers per skill.
     *
     * @param userId user ID
     * @return list of skill candidates with offer counts
     */
    List<SkillCandidateDto> getOfferedSkills(long userId);

    /**
     * Assigns a skill to the user based on offers.
     *
     * <p>Requires that the number of offers for the skill and user
     * is at least the configured threshold ({@code skill.offers.min.count}).</p>
     *
     * @param skillId skill ID
     * @param userId  user ID
     * @throws ForbiddenException if the number of offers is below the threshold
     */
    void acquireSkillFromOffers(long skillId, long userId);
}
