package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final int MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private final SkillMapper mapper;

    public SkillDto save(SkillDto skillDto) {

        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("Skill with title '" + skillDto.getTitle() + "' already exists.");
        }
        final Skill skillEntity = skillRepository.save(
                mapper.toEntity(skillDto)
        );

        return mapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(long userId) {
        final List<Skill> skills = skillRepository.findAllByUserId(userId);
        return mapper.toListDto(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);

        return offeredSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting())) // Группируем по объекту Skill
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(mapper.toDto((entry.getKey())), entry.getValue())) // Используем оригинальный объект Skill
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> existingSkillOpt = skillRepository.findUserSkill(skillId, userId);

        if (existingSkillOpt.isPresent()) {
            return mapper.toDto(existingSkillOpt.get());
        }

        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        for (SkillOffer skillOffer : skillOffers) {
            System.out.println(skillOffer);
        }
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new IllegalStateException("Skill was not offered enough times to acquire.");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        for (SkillOffer offer : skillOffers) {
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setId(userId);
            guarantee.setSkill(skillRepository.findById(skillId).get());
            guarantee.setGuarantor(userRepository.findById(userId).get());
            userSkillGuaranteeRepository.save(guarantee);
        }

        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId)
                .orElseThrow(() -> new IllegalStateException("Skill assignment failed."));
        return mapper.toDto(assignedSkill);
    }
}
