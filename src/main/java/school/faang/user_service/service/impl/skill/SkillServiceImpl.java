package school.faang.user_service.service.impl.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.model.dto.skill.SkillCandidateDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.UserSkillGuarantee;
import school.faang.user_service.model.entity.recommendation.SkillOffer;
import school.faang.user_service.model.event.SkillAcquiredEvent;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillMapper skillMapper;
    private final SkillCandidateMapper skillCandidateMapper;
    private final SkillValidator skillValidator;
    private final SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Override
    @Transactional
    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateSkillByTitle(skillDto);
        Skill skillEntity = skillMapper.toEntity(skillDto);
        return skillMapper.toDto(skillRepository.save(skillEntity));
    }

    @Override
    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkillsByUserId = skillRepository.findAllByUserId(userId);
        return skillMapper.toDto(allSkillsByUserId);
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skillsOfferedToUser = skillRepository.findSkillsOfferedToUser(userId);
        return skillsOfferedToUser.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> skillCandidateMapper.toDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidator.validateOfferedSkill(skillId, userId);
        List<SkillOffer> allSkillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        skillValidator.validateSkillByMinSkillOffers(allSkillOffers.size(), skillId, userId);
        skillRepository.assignSkillToUser(skillId, userId);
        addUserSkillGuarantee(allSkillOffers);
        sendEvent(skillId, userId);
        return skillMapper.toDto(allSkillOffers.get(0).getSkill());
    }

    private void addUserSkillGuarantee(List<SkillOffer> skillOffers) {
        List<UserSkillGuarantee> guarantees = skillOffers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .user(offer.getRecommendation().getReceiver())
                        .skill(offer.getSkill())
                        .guarantor(offer.getRecommendation().getAuthor())
                        .build())
                .distinct()
                .toList();
        userSkillGuaranteeRepository.saveAll(guarantees);
    }

    @Override
    public List<Skill> getSkillsByTitle(List<String> skillsTitle) {
        skillValidator.validateSkill(skillsTitle, skillRepository);
        return skillRepository.findByTitleIn(skillsTitle);
    }

    @Override
    public void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser(skillId, userId);
    }

    @Override
    public void deleteSkillFromGoal(long goalId) {
        skillRepository.deleteAll(skillRepository.findSkillsByGoalId(goalId));
    }

    private void sendEvent(long skillId, long userId) {
        SkillAcquiredEvent event = buildSkillAcquiredEvent(skillId, userId);
        skillAcquiredEventPublisher.publish(event);
    }

    private SkillAcquiredEvent buildSkillAcquiredEvent(long skillId, long userId) {
        return SkillAcquiredEvent.builder()
                .skillId(skillId)
                .userId(userId)
                .build();
    }
}