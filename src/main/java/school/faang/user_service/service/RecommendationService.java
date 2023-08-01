package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
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
import school.faang.user_service.validator.SkillChecker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    @Value("${min.offers.to.convert.skill:2}")
    private static int MIN_OFFERS_TO_CONVERT_SKILL;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillChecker skillChecker;

    /**
     * Метод, позволяющий пользователю дать рекомендацию другому пользователю в виде текста,
     * а также подтвердить определенный набор навыков у получателя рекомендации.
     *
     * @param recommendationDto
     * @return
     */
    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Long recommendationId = recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("entity not found exception"));
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();
        saveUserSkillsWithGuarantee(author, receiver, recommendationDto.getSkillOffers());
        List<SkillOffer> skillOffers = initNewSkillOffers(
                recommendationId,
                author,
                receiver,
                recommendationDto.getSkillOffers());
        recommendation.setSkillOffers(skillOffers);
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    /**
     * Метод, позволяющий пользователю править текст собственной рекомендации
     *
     * @param toUpdate - входящие параметры обновления в форме Dto
     * @return RecommendationDto (Dto обновленной рекомендации)
     */
    @Transactional
    public RecommendationDto update(RecommendationUpdateDto toUpdate) {
        Recommendation toUpdateRecommendation = recommendationRepository.findById(toUpdate.getId())
                .orElseThrow(() -> new DataValidationException("entity not found exception"));
        toUpdateRecommendation.setContent(toUpdate.getContent());
        toUpdateRecommendation.setUpdatedAt(LocalDateTime.now());
        return recommendationMapper.toDto(recommendationRepository.save(toUpdateRecommendation));
    }

    /**
     * Метод, предоставляющий возможность обновить все поля рекомендации, включая список офферов.
     * Также осуществляет добавление оферов в список скиллов согласно бизнес-логике
     * (если произошло накопление одинаковых офферов).
     * <p>
     * !! Не имеет представления в контроллере в текущей версии
     *
     * @param toUpdate - входящие параметры обновления в форме Dto
     * @return RecommendationDto (Dto обновленной рекомендации)
     */
    @Transactional
    public RecommendationDto extendedUpdate(RecommendationDto toUpdate) {
        skillOfferRepository.deleteAllByRecommendationId(toUpdate.getId());
        User author = userRepository.findById(toUpdate.getAuthorId())
                .orElseThrow(() -> new DataValidationException("entity not found exception"));
        User receiver = userRepository.findById(toUpdate.getReceiverId())
                .orElseThrow(() -> new DataValidationException("entity not found exception"));
        Recommendation recommendation = recommendationMapper.toEntity(toUpdate);
        saveUserSkillsWithGuarantee(author, receiver, toUpdate.getSkillOffers());
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        List<SkillOffer> skillOffers = initNewSkillOffers(
                recommendation.getId(),
                author,
                receiver,
                toUpdate.getSkillOffers());
        recommendation.setSkillOffers(skillOffers);
        recommendation.setUpdatedAt(toUpdate.getCreatedAt() != null ? toUpdate.getCreatedAt() : LocalDateTime.now());
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    /**
     * Метод, добавляющий скилл из рекомендации в список скилов,
     * если есть необходимое количество подтверждений у данного скила.
     * Также добавляет гаранторов из текущего и предыдущих оферов в список гаранторов к новому скилу.
     *
     * @param author
     * @param receiver
     * @param skillOffers
     */
    private void saveUserSkillsWithGuarantee(User author, User receiver, List<SkillOfferDto> skillOffers) {
        skillChecker.validate(skillOffers);
        List<Long> receiverSkillsIds = receiver.getSkills().stream()
                .map(Skill::getId)
                .toList();
        List<UserSkillGuarantee> guarantees = new ArrayList<>();
        skillOffers.forEach(s -> {
            long skillId = s.getSkillId();
            List<User> authors = skillOfferRepository.findAllAuthorsBySkillIdAndReceiverId(skillId, receiver.getId());
            if (authors != null && authors.size() >= MIN_OFFERS_TO_CONVERT_SKILL
                    && !receiverSkillsIds.contains(s.getSkillId())) {
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
        if (!guarantees.isEmpty()) {
            userSkillGuaranteeRepository.saveAll(guarantees);
        }
    }

    /**
     * Метод, в котором валидируем приходящие офера, формируем список оферов, добавляя гаранторов
     *
     * @param recommendationId
     * @param author
     * @param receiver
     * @param skillOffers
     * @return
     */
    private List<SkillOffer> initNewSkillOffers(Long recommendationId,
                                                User author,
                                                User receiver,
                                                List<SkillOfferDto> skillOffers) {
        skillChecker.validate(skillOffers);
        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        List<Skill> offeredSkills = skillRepository.findAllById(skillIds);
        addGuarantee(author, receiver, offeredSkills);
        List<Long> skillOfferIds = offeredSkills.stream()
                .map(s -> skillOfferRepository.create(s.getId(), recommendationId))
                .toList();
        List<SkillOffer> result = skillOfferRepository.findAllById(skillOfferIds);
        return result;
    }

    /**
     * Метод добавления автора рекомендации гарантом к скиллу, который он предлагает, если этот автор еще не стоит там гарантом
     *
     * @param author
     * @param receiver
     * @param offeredSkills
     * @return
     */
    private void addGuarantee(User author, User receiver, List<Skill> offeredSkills) {
        List<Skill> receiverSkills = receiver.getSkills();
        List<UserSkillGuarantee> userSkillGuarantees = new ArrayList<>();
        offeredSkills.forEach(s -> {
                    if (!receiverSkills.contains(s)) {
                        return;
                    }
                    boolean isGuaranteesContainsCurrentAuthor = s.getGuarantees()
                            .stream()
                            .map(UserSkillGuarantee::getGuarantor)
                            .map(User::getId)
                            .noneMatch(g -> g.equals(author.getId()));
                    if (isGuaranteesContainsCurrentAuthor) {
                        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                .skill(s)
                                .guarantor(author)
                                .user(receiver)
                                .build();
                        userSkillGuarantees.add(userSkillGuarantee);
                    }
                }
        );
        if(!userSkillGuarantees.isEmpty()) {
            userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
        }
    }
}
