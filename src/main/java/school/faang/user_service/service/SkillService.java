package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;
    private final int MIN_SKILL_OFFERS = 3;

    @Transactional
    public SkillDto create(SkillDto skillDTO) {
        if (skillRepository.existsByTitle(skillDTO.getTitle())) {
            throw new DataValidException("Skill already exists");
        }

        Skill skill = skillRepository.save(skillMapper.toEntity(skillDTO));
        return skillMapper.toDTO(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getUserSkills(long userId, int page, int size) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(skillMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        Map<SkillOffer, Long> groupedSkills = skillOfferRepository.findAllOffersToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return groupedSkills.entrySet()
                .stream()
                .map(entry -> SkillCandidateDto.builder()
                        .skill(skillMapper.toDTO(entry.getKey().getSkill()))
                        .offersAmount(entry.getValue())
                        .build())
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidException("Not enough offers");
        }
        skillRepository.assignSkillToUser(skillId, userId);

        return skillRepository.findById(skillId).map(skill -> {
            addGuarantees(skill, offers, userId);
            return skillMapper.toDTO(skill);
        }).orElseThrow(() ->
                new DataValidException("User skill not found"));
    }

    private void addGuarantees(Skill skill, List<SkillOffer> offers, Long userId) {
        User user = skill.getUsers().stream()
                .filter(e -> e.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new DataValidException("User not found"));

        List<UserSkillGuarantee> skillGuarantees = offers.stream().map(offer -> UserSkillGuarantee.builder()
                        .user(user)
                        .skill(skill)
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .toList();

        skill.addGuarantees(skillGuarantees);

        offers.forEach(offer -> skillOfferRepository.deleteById(offer.getId()));
    }
}
