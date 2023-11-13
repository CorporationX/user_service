package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        checkAndAddSkillsGuarantorAndUpdateUserSkills(recommendationDto);
        return recommendationDto;
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        checkAndAddSkillsGuarantorAndUpdateUserSkills(recommendation);
        return recommendation;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        Page<Recommendation> userRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return new PageImpl<>(recommendationMapper.toRecommendationDtos(userRecommendations.getContent()));
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        Page<Recommendation> authorRecommendations = recommendationRepository.findAllByAuthorId(authorId, pageable);
        return new PageImpl<>(recommendationMapper.toRecommendationDtos(authorRecommendations.getContent()));
    }

    public void checkAndAddSkillsGuarantorAndUpdateUserSkills(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();

        List<SkillOfferDto> skillOffersDto = recommendationDto.getSkillOffers();
        List<Skill> userSkills = skillRepository.findAllByUserId(receiverId);

        User receiver = findById(receiverId);
        User author = findById(authorId);

        processSkillGuarantors(userSkills, skillOffersDto, receiver, author);
        updateReceiverUserSkills(userSkills, skillOffersDto, receiver);
        saveSkillOffers(skillOffersDto);
    }

    public List<Skill> getOfferedSkillsFromDatabase(List<SkillOfferDto> skillOffersDto) {
        List<Long> skillsId = skillOffersDto.stream()
                .map(SkillOfferDto::getSkillId)
                .toList();
        return skillRepository.findAllById(skillsId);
    }

    private void processSkillGuarantors(List<Skill> userSkills, List<SkillOfferDto> skillOffersDto, User receiver, User author) {
        userSkills.stream()
                .filter(userSkill -> skillOffersDto.stream()
                        .anyMatch(offeredSkill -> userSkill.getId() == offeredSkill.getId()))
                .forEach(sameSkill -> {
                    if (isAuthorNotGuarantor(sameSkill, author.getId())) {
                        log.info("The author with ID: {} is not listed as a guarantor for the Skill with ID: {}", author.getId(), sameSkill.getId());
                        addGuarantorToSkill(sameSkill, receiver, author);
                    }
                    skillOffersDto.removeIf(skillOfferDto -> skillOfferDto.getSkillId() == sameSkill.getId());
                });
    }

    private void updateReceiverUserSkills(List<Skill> userSkills, List<SkillOfferDto> skillOffersDto, User receiver) {
        List<Skill> newSkills = getOfferedSkillsFromDatabase(skillOffersDto);

        newSkills.forEach(skill -> addGuarantorToSkill(skill, receiver, receiver));
        userSkills.addAll(newSkills);

        receiver.setSkills(userSkills);
        userRepository.save(receiver);
    }

    private void addGuarantorToSkill(Skill skill, User receiver, User guarantor) {
        UserSkillGuarantee newUserSkillGuarantee = buildUserSkillGuarantee(skill, receiver, guarantor);

        List<UserSkillGuarantee> skillGuarantees = Optional.ofNullable(skill.getGuarantees())
                .orElse(new ArrayList<>());
        skillGuarantees.add(newUserSkillGuarantee);
    }

    private void saveSkillOffers(List<SkillOfferDto> skillOffersDto) {
        skillOffersDto.forEach(offer -> skillOfferRepository.create(offer.getSkillId(), offer.getRecommendationId()));
    }

    private boolean isAuthorNotGuarantor(Skill userSkill, long authorId) {
        return userSkill.getGuarantees().stream()
                .map(UserSkillGuarantee::getGuarantor)
                .map(User::getId)
                .noneMatch(guaranteeId -> guaranteeId.equals(authorId));
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();

        if (authorId == receiverId) {
            throw new DataValidationException("Author cannot be the same as the receiver");
        } else if (recommendationDto.getSkillOffers() != null) {
            Optional<Recommendation> optionalRecommendation = recommendationRepository.
                    findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

            if (optionalRecommendation.isPresent()) {
                LocalDateTime recommendationCreateTime = optionalRecommendation.get().getCreatedAt();

                if (recommendationCreateTime.isAfter(LocalDateTime.now().minusMonths(6))) {
                    throw new DataValidationException("The recommendation must have been issued no earlier than 6 months ago");
                } else {
                    recommendationDto.getSkillOffers()
                            .forEach(skill -> validateSkillExistence(skill.getSkillId()));
                }
            }
        }
    }

    private void validateSkillExistence(long skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new DataValidationException(String.format("Skill with ID %d does not exist", skillId));
        }
    }

    private UserSkillGuarantee buildUserSkillGuarantee(Skill skill, User receiver, User guarantor) {
        return UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(guarantor)
                .build();
    }

    private User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d does not exist", userId)));
    }
}