package school.faang.user_service.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final static int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;

    @Transactional
    public SkillDto createSkill(SkillDto skillDto) {
        validateSkillDto(skillDto);
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill " + skillDto.getTitle() + " already exists");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        List<User> users = userRepository.findAllById(skillDto.getUserIds());
        skill.setUsers(users);
        return skillMapper.toDto(skillRepository.save(skill));
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillMapper.toDto(skillRepository.findAllByUserId(userId));
    }

    public List<SkillCandidateDto> getOfferedSkills(long usedId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(usedId);
        List<SkillDto> offeredSkillDtos = skillMapper.toDto(offeredSkills);
        Map<SkillDto, Long> skills = new HashMap<>();
        List<SkillCandidateDto> skillCandidateDtos = new ArrayList<>();

        offeredSkillDtos.forEach(skillDto -> skills.put(skillDto, skills.getOrDefault(skillDto, 0L) + 1));
        skills.forEach((key, value) -> skillCandidateDtos.add(new SkillCandidateDto(key, value)));
        return skillCandidateDtos;
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillRepository.getById(skillId);
        Optional<Skill> userSkillOptional = skillRepository.findUserSkill(skillId, userId);
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (userSkillOptional.isPresent() || offers.size() < MIN_SKILL_OFFERS) {
            return skillMapper.toDto(skill);
        }

        User user = userRepository.getById(userId);
        List<UserSkillGuarantee> guarantees = new ArrayList<>();

        offers.forEach(offer -> guarantees.add(UserSkillGuarantee.builder()
                .user(user)
                .skill(skill)
                .guarantor(offer.getRecommendation().getAuthor())
                .build())
        );
        userSkillGuaranteeRepository.saveAll(guarantees);
        skillRepository.assignSkillToUser(skillId, userId);
        return skillMapper.toDto(skillRepository.getById(skillId));
    }

    private void validateSkillDto(SkillDto skillDto) {
        String title = skillDto.getTitle();
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new DataValidationException("Empty skillDto title");
        }
    }
}
