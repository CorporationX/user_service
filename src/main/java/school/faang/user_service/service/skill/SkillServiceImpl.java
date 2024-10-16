package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;

    public SkillDto create(SkillDto skillDto) {
        if (!skillRepository.existsByTitle(skillDto.getTitle())) {
            Skill skill = skillMapper.toEntity(skillDto);
            skill = skillRepository.save(skill);
            return skillMapper.toDto(skill);
        }
        throw new DataValidationException("skill already exists");
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> tempSkills = skillRepository.findAllByUserId(userId);
        return tempSkills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> tempSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Integer> skillOffered = new HashMap<>();

        for (Skill skill : tempSkills) {
            skillOffered.putIfAbsent(skill, 0);
            skillOffered.put(skill, skillOffered.get(skill) + 1);
        }

        List<SkillCandidateDto> result = new ArrayList<>();
        for (Map.Entry<Skill, Integer> entry : skillOffered.entrySet()) {
            SkillCandidateDto skillCandidateDto = skillCandidateMapper.toSkillCandidateDto(entry.getKey());
            skillCandidateDto.setOffersAmount(entry.getValue());
            result.add(skillCandidateDto);
        }

        return result;
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.findUserSkill(skillId, userId).orElseThrow();
        User userToGuarantee = userRepository.findById(userId).orElse(null);

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skillOffers.size() >= 3) {
            skillRepository.assignSkillToUser(skillId, userId);
            List<UserSkillGuarantee> userSkillGuaranteeList = new ArrayList<>();

            skillOffers.forEach(skillOffer -> {
                User guarantorUser = userRepository.findById(skillOffer.getId()).orElse(null);
                UserSkillGuarantee guarantee = new UserSkillGuarantee();
                guarantee.setUser(userToGuarantee);
                guarantee.setSkill(skill);
                guarantee.setGuarantor(guarantorUser);
                userSkillGuaranteeList.add(guarantee);

            });
            skill.setGuarantees(userSkillGuaranteeList);
            skillRepository.save(skill);
        }

        return skillMapper.toDto(skill);
    }
}
