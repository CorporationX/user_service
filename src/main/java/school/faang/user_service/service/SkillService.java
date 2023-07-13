package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {


    private final SkillRepository skillRepository;

    private final SkillOfferRepository skillOfferRepository;

    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final SkillMapper skillMapper;

    private final static long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Такой скилл уже существует!!!");
        }
        Skill skill = skillRepository.save(skillMapper.toEntity(skillDto));

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new SkillCandidateDto(skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);

        if (userSkill.isEmpty()) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                userSkill = skillRepository.findById(skillId);
                addUserSkillGuaranteeRepository(skillRepository.findById(skillId), allOffersOfSkill);
                return skillMapper.toDto(userSkill.orElseThrow(() -> new DataValidationException("Такой скилла не существует!!")));
            }
        }
        return skillMapper.toDto(userSkill.orElseThrow(() -> new DataValidationException("Такой скилла не существует!!")));
    }

    protected void addUserSkillGuaranteeRepository(Optional<Skill> userSkill, List<SkillOffer> allOffersOfSkill) {
        for (SkillOffer skillOffer : allOffersOfSkill) {
            userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                    .user(skillOffer.getRecommendation().getReceiver())
                    .skill(userSkill.get())
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build());
        }
    }
}
