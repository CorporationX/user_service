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
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final UserService userService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    private final static int MIN_SKILL_OFFERS = 3;

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(long userId) {
        userService.checkUserById(userId);
        List<Skill> skillsOfUsers = skillRepository.findAllByUserId(userId);
        return skillsOfUsers.stream().map(skillMapper::toDTO).toList();
    }

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        Skill skillFromDto = skillMapper.toEntity(skillDto);
        if (skillRepository.existsByTitle(skillFromDto.getTitle())) {
            throw new DataValidationException("The skill already exists");
        }
        return skillMapper.toDTO(skillRepository.save(skillFromDto));
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> offeredSkillsAndCount = offeredSkills.stream()
                .collect(Collectors.toMap(Function.identity(), value -> 1L, Long::sum));
        return offeredSkillsAndCount.entrySet().stream().map(offeredSkill -> {
            SkillDto skillDto = skillMapper.toDTO(offeredSkill.getKey());
            return skillCandidateMapper.toDTO(skillDto, offeredSkill.getValue());
        }).toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("user have this skill already");
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillOffers.stream().filter(skillOffer ->
                        Collections.frequency(skillOffers, skillOffer.skill.getTitle()) >= MIN_SKILL_OFFERS)
                .forEach(skillOffer -> {
                    Skill skill = skillOffer.getSkill();
                    if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
                        skillRepository.assignSkillToUser(skillId, userId);
                        saveUserSkillGuarantee(skillOffer, skill);
                    } else {
                        saveUserSkillGuarantee(skillOffer, skill);
                    }
                });
        Skill result = skillRepository.findUserSkill(skillId, userId).get();
        return skillMapper.toDTO(result);
    }

    private void saveUserSkillGuarantee(SkillOffer skillOffer, Skill skill) {
        userSkillGuaranteeRepository.save(UserSkillGuarantee
                .builder().user(skillOffer.getRecommendation().getReceiver())
                .skill(skill).guarantor(skillOffer.getRecommendation().getAuthor()).build());
    }
}
