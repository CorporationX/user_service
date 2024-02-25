package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserService userService;
    private static final long MIN_SKILL_OFFERS = 3;

    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill savedSkill = skillRepository.save(skillMapper.toEntity(skill));
            return skillMapper.toDto(savedSkill);
        } else {
            throw new DataValidationException("Такой навык уже существует");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {

        List<Skill> skillsByUserId = skillRepository.findAllByUserId(userId);
        return skillsByUserId.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {

        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        Map<Long, Long> skillCountMap = skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Skill::getId, Collectors.counting()));

        return skillsOfferedToUser.stream()
                .distinct()
                .map(skillCandidateMapper::toDto)
                .peek((skillCandidateDto -> {
                    long countSkill = skillCountMap.get(skillCandidateDto.getSkillDto().getId());
                    skillCandidateDto.setOffersAmount(countSkill);
                }))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {

        Skill skill = getSkillById(skillId);
        SkillDto skillDto = skillMapper.toDto(skill);
        User user = userService.findById(userId);

        if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

            if (allOffersOfSkill.size() >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                allOffersOfSkill.forEach((offer) -> {
                    User guarantor = new User();
                    guarantor.setId(offer.getRecommendation().getAuthor().getId());
                    userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                            .user(user)
                            .guarantor(guarantor)
                            .skill(skill).build());
                });
            }
        }
        return skillDto;
    }

    public Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> new EntityNotFoundException("Такого навыка не существует"));
    }

    public List<Skill> findSkillsByGoalId(long goalId) {
        return skillRepository.findSkillsByGoalId(goalId);
    }

    public boolean validateSkill(Skill skill) {
        return skillRepository.existsByTitle(skill.getTitle());
    }


    public void assignSkillToUser(long userId, long skillId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }


    public boolean existsById(long id) {
        return skillRepository.existsById(id);
    }

}