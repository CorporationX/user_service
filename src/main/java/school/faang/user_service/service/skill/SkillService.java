package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("Skill already exists");
        }

        Skill skillEntity = skillMapper.toEntity(skill);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(skillMapper::toSkillCandidateDto)
                .toList();
    }

    public SkillDto acquireSkillFromOffer(long skillId, long userId) {
        Skill offeredSkill = skillRepository.findUserSkill(skillId, userId).orElse(null);
        if (offeredSkill != null) {
            return skillMapper.toDto(offeredSkill);
        }

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException("Skill with " + skillId + " id not exists"));

        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException("Not enough offers");
        }

        skillRepository.assignSkillToUser(skillId, userId);
        List<UserSkillGuarantee> guarantees = setSkillGuaranteesAuthors(skill, offers);
        userSkillGuaranteeRepository.saveAll(guarantees);

        return skillMapper.toDto(skill);
    }

    private List<UserSkillGuarantee> setSkillGuaranteesAuthors(Skill skill, List<SkillOffer> skillOffers) {
        List<UserSkillGuarantee> guarantees = new ArrayList<>();

        skillOffers.forEach(skillOffer -> {
            UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .user(skillOffer.getRecommendation().getReceiver())
                    .skill(skill).build();
            guarantees.add(userSkillGuarantee);
        });

        return guarantees;
    }
}