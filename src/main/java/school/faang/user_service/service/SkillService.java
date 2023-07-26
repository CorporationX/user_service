package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    private final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);

        Skill skillToSave = skillMapper.toEntity(skillDto);
        List<User> users = StreamSupport.stream
                (userRepository.findAllById(skillDto.getUserIds()).spliterator(), false).toList();
        skillToSave.setUsers(users);
        
        return skillMapper.toDto(skillRepository.save(skillToSave));
    }

    private void validateSkill(SkillDto skillDto) {
        if (skillDto.getTitle().isBlank()) {
            throw new DataValidationException("Enter skill title, please");
        }

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title " + skillDto.getTitle() + " already exists");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills.stream()
                .map(skillMapper::toDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);

        return skillsOfferedToUser.stream().
                collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillMapper.toCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> offeredSkill = skillRepository.findUserSkill(skillId, userId);

        if (offeredSkill.isPresent()) {
            throw new DataValidationException("User already has this skill");
        }

        List<SkillOffer> offeredSkills = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offeredSkills.size() >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            addUserSkillGuarantee(offeredSkills);
        } else {
            log.debug("User ID {} doesn't have enough suggestions for skill ID {}. At least required {}, but received {}",
                    userId, skillId, MIN_SKILL_OFFERS, offeredSkills.size());
        }
        return skillMapper.toDto(offeredSkills.get(0).getSkill());
    }

    private void addUserSkillGuarantee(List<SkillOffer> offeredSkills) {
        userSkillGuaranteeRepository.saveAll(offeredSkills.stream()
                .map(offeredSkill -> UserSkillGuarantee.builder()
                        .user(offeredSkill.getRecommendation().getReceiver())
                        .skill(offeredSkill.getSkill())
                        .guarantor(offeredSkill.getRecommendation().getAuthor())
                        .build()
                )
                .distinct()
                .toList());
    }
}
