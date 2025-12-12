package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.repository.skill.UserSkillGuaranteeRepository;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final UserContext userContext;

    private static final int REQUIRED_OFFERS = 3;

    @Override
    @Transactional
    public SkillDto create(CreateSkillDto skillDto) {
        log.info("Create new skill: {}", skillDto.getTitle());
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new DataValidationException("A skill with this name already exists");
        }

        Skill skill = skillMapper.toSkill(skillDto);
        Skill saved = skillRepository.save(skill);
        return skillMapper.toSkillDto(saved);
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        log.info("Getting a list of skills for user id={}", userId);
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        log.info("Getting offered skills for user id={}", userId);

        return skillOfferRepository.findSkillsOfferedToUser(userId).stream()
                .map(skill -> {
                    int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
                    return new SkillCandidateDto(skillMapper.toSkillDto(skill), offersAmount);
                })
                .toList();
    }

    @Override
    @Transactional
    public void acquireSkillFromOffers(Long skillId, Long userId) {
        log.info("User id={} is trying to add skill id={}", userId, skillId);

        if (skillRepository.existsByUserIdAndSkillId(userId, skillId)) {
            throw new DataValidationException("The user already has skill");
        }

        int offerCount = skillOfferRepository.countAllOffersOfSkill(skillId, userId);
        if (offerCount < REQUIRED_OFFERS) {
            throw new ForbiddenException("No enough confirmation to assign skill");
        }

        skillRepository.assignSkillToUser(skillId, userId);

        var offers = skillOfferRepository.findAllBySkillIdAndOfferedUserId(skillId, userId);
        List<UserSkillGuarantee> guarantees = offers.stream()
                .map(offer -> new UserSkillGuarantee(null, offer.getOfferedBy(), offer.getSkill(), offer.getAuthor()))
                .toList();

        userSkillGuaranteeRepository.saveAll(guarantees);

        log.info("Skill id={} successfully assigned to user id={}", skillId, userId);
    }
}