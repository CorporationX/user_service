package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recomendation.PageDto;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        log.info("Запрос на добавление рекомендации пользователю с ID: {}, от пользователя с ID: {}",
                recommendation.getReceiverId(), recommendation.getAuthorId());
        validateRecommendationBeforeCreatingAndUpdating(recommendation);

        createSkillOffer(recommendation);
        existsUserSkill(recommendation);

        return recommendationMapper.toDto(recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent())
        );
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        log.info("Запрос на обновление рекомендации пользователю с ID: {}, от пользователя с ID: {}",
                recommendation.getReceiverId(), recommendation.getAuthorId());
        validateRecommendationBeforeCreatingAndUpdating(recommendation);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        createSkillOffer(recommendation);
        existsUserSkill(recommendation);

        return recommendationMapper.toDto(recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent())
        );
    }

    @Transactional
    public void delete(long id) {
        log.info("Удаляем рекомендацию с ID: {}", id);
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, PageDto page) {
        log.info("Запрос всех рекомендаций пользователя с ID: {}", receiverId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByReceiverId(receiverId, PageRequest.of(page.getPage(), page.getPageSize()));
        return recommendations.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, PageDto page) {
        log.info("Запрос на получение всех рекомендаций автора с ID: {}", authorId);
        Page<Recommendation> recommendations = recommendationRepository
                .findAllByAuthorId(authorId, PageRequest.of(page.getPage(), page.getPageSize()));
        return recommendations.map(recommendationMapper::toDto);
    }

    private void validateRecommendationBeforeCreatingAndUpdating(RecommendationDto recommendation) {
        validateLastRecommendationTime(recommendation);
        validateSkillExistence(recommendation);
        validateSkillOffersExistence(recommendation);
    }

    private void validateLastRecommendationTime(RecommendationDto recommendation) {
        log.info("Старт  validateLastRecommendationTime, recommendationDto с ID: {}", recommendation.getId());
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(),
                recommendation.getReceiverId()
        ).ifPresent(foundRecommendation -> {
            if (foundRecommendation.getCreatedAt().isBefore(LocalDateTime.now().minusMonths(6))) {
                log.error("Рекомендация от автора с ID: {}, к получателю с ID: {},не возможна т.к.с момента последней" +
                                "рекомендаций от {}, не прошло 6 месяцев!",
                        recommendation.getAuthorId(),
                        recommendation.getReceiverId(),
                        recommendation.getCreatedAt().toString());
                throw new DataValidationException("Не прошло 6 месяцев с момента последней рекомендации!");
            }
        });
    }

    private void validateSkillExistence(RecommendationDto recommendation) {
        log.info("Старт  validateSkillExistence, recommendationDto с ID: {}", recommendation.getId());
        List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();
        skillOfferDtos.forEach(skillOfferDto -> skillOfferRepository.findById(skillOfferDto.getSkillId())
                .orElseThrow(() -> new DataValidationException("Навык c ID, " + skillOfferDto.getSkillId() + " не существуют в системе")));
    }

    private void validateSkillOffersExistence(RecommendationDto recommendation) {
        log.info("Старт  validateSkillOffersExistence, recommendation с ID: {}", recommendation.getId());

        List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            log.error("Отсутствуют навыки в рекомендации с ID: {}", recommendation.getId());
            throw new DataValidationException("В рекомендации нет предложений по навыкам");
        }

        List<Long> skillIds = skillOfferDtos.stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> existingSkillIds = StreamSupport.stream(skillRepository.findAllById(skillIds)
                        .spliterator(), false)
                .map(Skill::getId)
                .distinct()
                .toList();

        skillIds.removeAll(existingSkillIds);
        if (!skillIds.isEmpty()) {
            throw new DataValidationException("Навыки которые отсутствуют в системе: " + skillIds);
        }
    }

    private void createSkillOffer(RecommendationDto recommendation) {
        recommendation.getSkillOffers()
                .forEach(skillOffer -> skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId()));
    }

    private void existsUserSkill(RecommendationDto recommendation) {
        List<Long> skillsIds = skillOfferRepository.findAllByUserId(recommendation.getReceiverId())
                .stream()
                .map(SkillOffer::getId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> skillOfferDtos = recommendation.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .distinct()
                .toList();

        skillsIds.retainAll(skillOfferDtos);
        skillsIds.forEach(skillId -> addUserSkillGuarantee(recommendation.getAuthorId(),
                recommendation.getReceiverId(), skillId));
    }

    private void addUserSkillGuarantee(Long authorId, Long receiverId, Long skillId) {
        User author = userMapper.toUser(userService.getUserById(authorId));
        User receiver = userMapper.toUser(userService.getUserById(receiverId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Навык не найден"));
        userSkillGuaranteeRepository.save(new UserSkillGuarantee(null, receiver, skill, author));
    }
}