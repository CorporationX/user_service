package school.faang.user_service.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.RecommendationDto;
import school.faang.user_service.model.dto.SkillOfferDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.UserSkillGuarantee;
import school.faang.user_service.model.entity.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.model.event.RecommendationReceivedEvent;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.model.event.SkillOfferedEvent;
import school.faang.user_service.publisher.SkillOfferedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.RecommendationRepository;
import school.faang.user_service.repository.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private static final int NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;
    private final SkillOfferedEventPublisher skillOfferedEventPublisher;

    @Override
    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        Long recommendationId = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        recommendationDto.setId(recommendationId);
        addSkillOffersAndGuarantee(recommendationDto);

        publishSkillOfferedEvent(recommendationDto);

        Recommendation recommendation = recommendationRepository.findById(recommendationDto.getId()).orElseThrow(() -> new NoSuchElementException(
                String.format("There is no recommendation with id = %d", recommendationDto.getId())));
        recommendationReceivedEventPublisher.publish(new RecommendationReceivedEvent(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendation.getId()));
        return recommendationMapper.toDto(recommendation);
    }

    @Override
    @Transactional
    public RecommendationDto update(long id, RecommendationDto recommendationDto) {
        if (recommendationDto.getId() != null && recommendationDto.getId() != id) {
            throw new DataValidationException("Mismatched id in the URL and the body");
        }
        recommendationDto.setId(id);
        checkIfOfferedSkillsExist(recommendationDto);
        checkIfAcceptableTimeForRecommendation(recommendationDto);

        recommendationRepository.update(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        addSkillOffersAndGuarantee(recommendationDto);

        publishSkillOfferedEvent(recommendationDto);

        Recommendation updatedRecommendation = recommendationRepository.findById(recommendationDto.getId()).orElseThrow(() -> new NoSuchElementException(
                String.format("There is no recommendation with id = %d", recommendationDto.getId())));
        return recommendationMapper.toDto(updatedRecommendation);
    }

    @Override
    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendations = recommendationRepository.findAllByReceiverId(receiverId, Pageable.unpaged());
        return recommendationMapper.toDtoList(recommendations.getContent());
    }

    private void addSkillOffersAndGuarantee(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId());
            skillRepository.findUserSkill(skillOfferDto.getSkillId(), skillOfferDto.getReceiverId())
                    .ifPresent(skill -> {
                        if (!isAuthorAlreadyGuarantor(skillOfferDto, skill)) {
                            addGuaranteeToSkill(skillOfferDto, skill);
                        }
                    });
        }
    }

    private boolean isAuthorAlreadyGuarantor(SkillOfferDto skillOfferDto, Skill skill) {
        return skill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .anyMatch(skillOfferDto.getAuthorId()::equals);
    }

    private void addGuaranteeToSkill(SkillOfferDto skillOfferDto, Skill skill) {
        User receiver = userRepository.findById(skillOfferDto.getReceiverId())
                .orElseThrow(() -> new NoSuchElementException(String.format("There isn't receiver with id = %d",
                        skillOfferDto.getReceiverId())));

        User author = userRepository.findById(skillOfferDto.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException(String.format("There isn't author of recommendation with id = %d",
                        skillOfferDto.getAuthorId())));

        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build();

        skill.getGuarantees().add(guarantee);
        skillRepository.save(skill);
    }

    private void checkIfAcceptableTimeForRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId())
                .ifPresent(recommendation -> {
                    if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        throw new DataValidationException(
                                String.format("Author id = %s did recommendation for user id = %s less than %d months ago",
                                        recommendationDto.getAuthorId(),
                                        recommendationDto.getReceiverId(),
                                        NUMBER_OF_MONTHS_AFTER_PREVIOUS_RECOMMENDATION));
                    }
                });
    }

    private void checkIfOfferedSkillsExist(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        List<Long> skillIdList = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();

        int crossedSkillAmount = skillRepository.countExisting(skillIdList);
        if (crossedSkillAmount < skillIdList.size()) {
            throw new DataValidationException("Not all skills exist in the system");
        }
    }

    private void publishSkillOfferedEvent(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOfferDtoList = recommendationDto.getSkillOffers();
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        log.info("Starting to publish {} skill offers for recommendation ID: {}",
                skillOfferDtoList.size(), recommendationDto.getId());

        for (SkillOfferDto skillOfferDto : skillOfferDtoList) {
            skillOfferedEventPublisher.publish(new SkillOfferedEvent(
                    skillOfferDto.getAuthorId(), skillOfferDto.getReceiverId(),  skillOfferDto.getSkillId()));
            log.info("Published SkillOfferedEvent - Author ID: {}, Receiver ID: {}, Skill ID: {}",
                    skillOfferDto.getAuthorId(), skillOfferDto.getReceiverId(), skillOfferDto.getSkillId());
        }
    }
}
