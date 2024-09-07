package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillServiceValidator;

    public SkillDto create(SkillDto skillDto) {
        skillServiceValidator.validateSkillByTitle(skillDto);
        Skill skillEntity = skillMapper.toEntity(skillDto);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkillsByUserId = skillRepository.findAllByUserId(userId);
        return skillMapper.toDto(allSkillsByUserId);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);
        return skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillServiceValidator.validateOfferedSkill(skillId, userId);
        List<SkillOffer> allSkillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillServiceValidator.validateSkillByMinSkillOffers(allSkillOffers.size(), skillId, userId);
        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(allSkillOffers);
        return skillMapper.toDto(allSkillOffers.get(0).getSkill());
    }

    private void addUserSkillGuarantee(List<SkillOffer> offeredSkills) {
        userSkillGuaranteeRepository.saveAll(offeredSkills.stream()
                .map(offeredSkill -> UserSkillGuarantee.builder()
                        .user(offeredSkill.getRecommendation().getReceiver())
                        .skill(offeredSkill.getSkill())
                        .guarantor(offeredSkill.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList());
    }
}