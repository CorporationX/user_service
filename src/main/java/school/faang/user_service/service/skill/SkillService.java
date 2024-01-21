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
import school.faang.user_service.mapper.skill.SkillMapper;
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

    private final int MIN_SKILL_OFFERS = 3;

    private final SkillMapper skillMapper;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    public SkillDto create (SkillDto skill) {

        getSkillFromDB(skill.getTitle());

        Skill skillEntity = skillMapper.toEntity(skill);
        List<User> users = userRepository.findAllById(skill.getUserIds());
        skillEntity.setUsers(users);

        skillEntity = skillRepository.save(skillEntity);

        return skillMapper.toDto(skillEntity);
    }

    public SkillDto acquireSkillFromOffers (long skillId, long userId) {
        Skill skill = skillRepository.findUserSkill(skillId, userId).orElse(null);
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        if (skill == null && offers.size() >= MIN_SKILL_OFFERS) {
            skill = skillRepository.findById(skillId).orElseThrow(
                    () -> new DataValidationException("Skill doesn't exist!")
            );

            skillRepository.assignSkillToUser(skillId, userId);

            for (SkillOffer offer : offers) {
                User receiver = offer.getRecommendation().getReceiver();
                User author = offer.getRecommendation().getAuthor();

                UserSkillGuarantee guarantor = UserSkillGuarantee.builder()
                        .id(userId)
                        .user(receiver)
                        .skill(skill)
                        .guarantor(author)
                        .build();

                userSkillGuaranteeRepository.save(guarantor);
                setSkillGuarantees(offers, skill, userId);
            }
        } else {
            throw new DataValidationException("Not enough offers");
        }

        return skillMapper.toDto(skill);
    }

    private void setSkillGuarantees(List<SkillOffer> offers, Skill skill, long userId) {

        for (SkillOffer offer : offers) {
            User receiver = offer.getRecommendation().getReceiver();
            User author = offer.getRecommendation().getAuthor();

            UserSkillGuarantee guarantor = UserSkillGuarantee.builder()
                    .id(userId)
                    .user(receiver)
                    .skill(skill)
                    .guarantor(author)
                    .build();

            userSkillGuaranteeRepository.save(guarantor);
        }
    }

    public List<SkillDto> getUserSkills (long userId) {

        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skillMapper.listToDto(skills);
    }

    public List<SkillCandidateDto> getOfferedSkills (long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        List<SkillDto> offeredSkillDtos = skillMapper.listToDto(offeredSkills);

        Map<SkillDto, Long> offeredSkillDtosMap = offeredSkillDtos
                .stream()
                .collect(Collectors.groupingBy(skillDto -> skillDto, Collectors.counting()));

        List<SkillCandidateDto> offeredSkillCandidateDtos = offeredSkillDtosMap
                .entrySet().stream()
                .map(item -> new SkillCandidateDto(item.getKey(), item.getValue()))
                .toList();

        return offeredSkillCandidateDtos;
    }

    public void getSkillFromDB (String skillTitle) {
        if (skillRepository.existsByTitle(skillTitle)) {
            throw new DataValidationException("Skill with name " + skillTitle + " already exists in database.");
        }
    }
}
