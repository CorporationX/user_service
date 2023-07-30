package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillValidator skillValidator;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Long id = recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        Recommendation recommendation = recommendationRepository.findById(id).orElseThrow();
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();
        saveUserSkills(author, receiver, recommendationDto.getSkillOffers());
        List<SkillOffer> skillOffers = initNewSkillOffers(
                id,
                author,
                receiver,
                recommendationDto.getSkillOffers());
        recommendation.setSkillOffers(skillOffers);
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    @Transactional
    public RecommendationDto update(RecommendationDto updated) {
        if (updated.getId() == null) {
            throw new DataValidationException("entity not found exception");
        }
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());
        User author = userRepository.findById(updated.getAuthorId()).orElseThrow();
        User receiver = userRepository.findById(updated.getReceiverId()).orElseThrow();
        Recommendation recommendation = recommendationMapper.toEntity(updated);
        saveUserSkills(author, receiver, updated.getSkillOffers());
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        List<SkillOffer> skillOffers = initNewSkillOffers(
                recommendation.getId(),
                author,
                receiver,
                updated.getSkillOffers());
        recommendation.setSkillOffers(skillOffers);
        recommendation.setUpdatedAt(updated.getCreatedAt() != null ? updated.getCreatedAt() : LocalDateTime.now());
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    private List<SkillOffer> initNewSkillOffers(Long recommendationId,
                                                User author,
                                                User receiver,
                                                List<SkillOfferDto> skillOffers) {
        skillValidator.validate(skillOffers);
        List<Skill> offeredSkills = skillRepository.findAllById(
                skillOffers.stream()
                        .map(SkillOfferDto::getSkillId)
                        .toList());
        addGuarantee(author, receiver, offeredSkills);
        return skillOfferRepository.findAllById(
                offeredSkills.stream()
                        .map(s -> skillOfferRepository.create(s.getId(), recommendationId))
                        .toList());
    }

    private void addGuarantee(User author, User receiver, List<Skill> offeredSkills) {
        List<Skill> receiverSkills = receiver.getSkills();
        List<UserSkillGuarantee> userSkillGuarantees = new ArrayList<>();
        offeredSkills.forEach(
                s -> {
                    if (receiverSkills.contains(s)) {
                        if (s.getGuarantees()
                                .stream()
                                .map(UserSkillGuarantee::getGuarantor)
                                .map(User::getId)
                                .noneMatch(g -> g.equals(author.getId()))) {
                            UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                    .skill(s)
                                    .guarantor(author)
                                    .user(receiver)
                                    .build();
                            userSkillGuarantees.add(userSkillGuarantee);
                        }
                    }
                }
        );
        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }

    private void saveUserSkills(User author, User receiver, List<SkillOfferDto> skillOffers) {
        skillValidator.validate(skillOffers);
        List<Long> receiverSkillsIds = receiver.getSkills().stream()
                .map(Skill::getId)
                .toList();
        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        skillOffers.forEach(s -> {
            long skillId = s.getSkillId();
            List<User> authors = skillOfferRepository.findAllAuthorsBySkillIdAndReceiverId(skillId, receiver.getId());
            if(authors != null && authors.size()>=2
                    && !receiverSkillsIds.contains(s.getSkillId())){
                skillRepository.assignSkillToUser(skillId, receiver.getId());
                authors.forEach(o -> guarantees.add(UserSkillGuarantee.builder()
                        .skill(Skill.builder().id(skillId).build())
                        .guarantor(o)
                        .user(receiver)
                        .build()));
                guarantees.add(UserSkillGuarantee.builder()
                        .skill(Skill.builder().id(skillId).build())
                        .guarantor(author)
                        .user(receiver)
                        .build());
            }
        });
        if(!guarantees.isEmpty()){
            userSkillGuaranteeRepository.saveAll(guarantees);
        }
    }
}
