package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {
    private static final long MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private List<UserSkillGuarantee> list3;

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException();
        }
            Skill skillEntity = skillMapper.toEntity(skillDto);
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkills = skillRepository.findAllByUserId(userId);
        return allSkills.stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> allOfferedSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> map2 = allOfferedSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        List<SkillCandidateDto> listFinal = map2.entrySet().stream()
                .map(entryPair -> new SkillCandidateDto(skillMapper.toDto(entryPair.getKey()),
                        entryPair.getValue()))
                .toList();
        return listFinal;
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Optional<Skill> requiredSkill = skillRepository.findUserSkill(skillId, userId);
        if (requiredSkill.isEmpty()) {
            List<SkillOffer> allOffers =
                    skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            long count = allOffers.size();
            if (count >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                Skill skill1 = skillRepository.findById(skillId).orElseThrow(() ->
                        new RuntimeException("Скилл не найден"));
                User user4 = userRepository.findById(userId).orElseThrow(() ->
                        new RuntimeException("Пользователь не найден"));
                for (SkillOffer skillOffer : allOffers) {
                    UserSkillGuarantee obj = UserSkillGuarantee.builder()
                            .user(user4)
                            .skill(skill1)
                            .guarantor(skillOffer.getRecommendation().getAuthor())
                            .build();
                    userSkillGuaranteeRepository.save(obj);
                    skill1.addGuarantee(obj);
                }
                skillRepository.save(skill1);
                return skillMapper.toDto(skill1);
            }
        }
    }
}

