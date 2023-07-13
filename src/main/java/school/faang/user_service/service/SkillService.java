package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.util.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static school.faang.user_service.util.Message.SKILL_NOT_FOUND;
import static school.faang.user_service.util.Message.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    public static final int MIN_SKILL_OFFERS = 3;

    @Transactional
    public SkillDto create(SkillDto skill) {
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill newSkill = SkillMapper.INSTANCE.skillToEntity(skill);
            skillRepository.save(newSkill);

            return SkillMapper.INSTANCE.skillToDto(newSkill);
        }

        throw new DataValidationException("Skill " + skill.getTitle() + " already exists");
    }

    public List<SkillDto> getUserSkills(Long userId, int pageNumber, int pageSize) {
        int numToSkip = (pageNumber - 1) * pageSize;

        return skillRepository.findAllByUserId(userId)
                .stream()
                .skip(numToSkip)
                .limit(pageSize)
                .map(SkillMapper.INSTANCE::skillToDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        Map<Skill, Long> skillMap = skillRepository.findAllByUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));

        return skillMap.entrySet()
                .stream()
                .map(skillEntry -> SkillCandidateDto.builder()
                        .skill(SkillMapper.INSTANCE.skillToDto(skillEntry.getKey()))
                        .offersAmount(skillEntry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Optional<Skill> optionalSkill = skillRepository.findUserSkill(skillId, userId);
        if (optionalSkill.isPresent()) {
            return SkillMapper.INSTANCE.skillToDto(optionalSkill.get());
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new RuntimeException(Message.NOT_ENOUGH_OFFERS);
        }

        skillRepository.assignSkillToUser(skillId, userId);

        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new RuntimeException(String.format(SKILL_NOT_FOUND, skillId, userId)));

        Skill updatedSkill = addSkillGuarantees(assignedSkill, skillOffers, userId);

        return SkillMapper.INSTANCE.skillToDto(updatedSkill);
    }

    private Skill addSkillGuarantees(Skill skill, List<SkillOffer> skillOffers, Long userId){
        List<UserSkillGuarantee> newGuarantees = skillOffers.stream().map(skillOffer -> UserSkillGuarantee.builder()
                    .user(userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException(String.format(USER_NOT_FOUND, userId))))
                    .skill(skillOffer.getSkill())
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build())
                .toList();
        skill.addGuarantees(newGuarantees);

        return skill;
    }
}