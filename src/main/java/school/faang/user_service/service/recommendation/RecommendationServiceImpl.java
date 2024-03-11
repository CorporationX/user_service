package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recomendation.PageDto;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SkillNotFoundException;
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
        validateRecommendation(recommendation);

        createSkillOffer(recommendation);
        addSkillsAsGuaranteedIfExists(recommendation);
        recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        return recommendation;
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        log.info("Запрос на обновление рекомендации пользователю с ID: {}, от пользователя с ID: {}",
                recommendation.getReceiverId(), recommendation.getAuthorId());
        validateRecommendation(recommendation);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        createSkillOffer(recommendation);
        addSkillsAsGuaranteedIfExists(recommendation);

        recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        return recommendation;
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

    private boolean allSkillsExistInSystem(List<SkillOfferDto> skillOffers) {
        return skillOffers.stream()
                .allMatch(skill -> skillRepository.existsById(skill.getSkillId()));
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        validateAuthorIsNotReceiver(recommendationDto);
        validateLastRecommendationTime(recommendationDto);
        validateSkillOffersExistence(recommendationDto);
    }

    private void validateAuthorIsNotReceiver(RecommendationDto recommendationDto) {
        if (recommendationDto.getAuthorId().equals(recommendationDto.getReceiverId())) {
            log.error("Ошибка валидации: author автор не может быть и receiver");
            throw new DataValidationException("author автор не может быть и receiver");
        }
    }

    private void validateLastRecommendationTime(RecommendationDto recommendationDto) {
        recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId()
        ).ifPresent(recommendation -> {
            if (recommendation.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6))) {
                log.error("Ошибка валидации: Не прошло 6 месяцев с момента последней рекомендации!");
                throw new DataValidationException("Не прошло 6 месяцев с момента последней рекомендации!");
            }
        });
    }

    private void validateSkillOffersExistence(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() != null &&
                !allSkillsExistInSystem(recommendationDto.getSkillOffers())) {
            log.error("Ошибка валидации: Навыки отсутствуют в нашей системе");
            throw new SkillNotFoundException("Навыки отсутствуют в нашей системе");
        }
    }

    private void createSkillOffer(RecommendationDto recommendation) {
        if (recommendation.getSkillOffers() != null) {
            recommendation.getSkillOffers().forEach(skillOffer -> {
                if (skillOffer != null && skillOffer.getSkillId() != null) {
                    skillOfferRepository.create(skillOffer.getSkillId(), recommendation.getId());
                } else {
                    log.error("Ошибка при создании предложенного навыка: skillOffer или skillId == null");
                    throw new SkillNotFoundException("Навык или его ID не может быть null");
                }
            });
        } else {
            log.info("Список skillOffers пуст и будет пропущен при создании рекомендации.");
        }
    }

    private void addSkillsAsGuaranteedIfExists(RecommendationDto recommendation) {
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
        skillsIds.forEach(skillId -> addUserSkillGuarantee(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                skillId));
    }

    private void addUserSkillGuarantee(Long authorId, Long receiverId, Long skillId) {
        User author = userMapper.toUser(userService.getUserById(authorId));
        User receiver = userMapper.toUser(userService.getUserById(receiverId));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException("Навык не найден"));
        userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(author)
                .build()
        );
    }
}