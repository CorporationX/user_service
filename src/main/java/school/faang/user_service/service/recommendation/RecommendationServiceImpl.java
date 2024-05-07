package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.Recommendation.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final RecommendationValidator validator;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validator.validateAuthorAndReceiver(recommendationDto);

        recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());

        processSkillsAndGuarantees(recommendationDto);

        return recommendationDto;
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(RecommendationDto updatedRecommendationDto) {
        validator.validateAuthorAndReceiver(updatedRecommendationDto);

        skillOfferRepository.deleteAllByRecommendationId(updatedRecommendationDto.getId());
        processSkillsAndGuarantees(updatedRecommendationDto);

        recommendationRepository.update(
                updatedRecommendationDto.getAuthorId(),
                updatedRecommendationDto.getReceiverId(),
                updatedRecommendationDto.getContent());

        return updatedRecommendationDto;
    }

    @Override
    public void delete(long id) {
        validator.checkIfRecommendationNotExist(id);

        recommendationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(0, Integer.MAX_VALUE));

        return recommendationPage.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationPage = recommendationRepository
                .findAllByAuthorId(
                        authorId,
                        PageRequest.of(0, Integer.MAX_VALUE));

        return recommendationPage
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    @Transactional
    public Participants getAuthorAndReceiver(RecommendationDto updated) {
        User author = findUserByUserId(updated.getAuthorId());
        User receiver = findUserByUserId(updated.getReceiverId());
        return new Participants(author, receiver);
    }

    public record Participants(User author, User receiver) {
    }


    @Transactional
    public void processSkillsAndGuarantees(RecommendationDto recommendationDto) {
        Participants participants = getAuthorAndReceiver(recommendationDto);

        List<Skill> existingReceiverSkills = skillRepository.findAllByUserId(participants.receiver.getId());
        List<SkillOfferDto> skillOffersDto = recommendationDto.getSkillOffers();

        if (skillOffersDto == null) {
            skillOffersDto = new ArrayList<>();
        }

        updateSkillsGuarantees(
                skillOffersDto,
                existingReceiverSkills,
                participants);

        saveNewSkills(
                participants,
                skillOffersDto);

        saveSkillOffers(skillOffersDto);
    }


    @Transactional
    public User findUserByUserId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with ID: " + userId + " does not exist"));
    }

    private void updateSkillsGuarantees(
            List<SkillOfferDto> skillOffersDto,
            List<Skill> receiverSkills,
            Participants participants) {

        List<Long> skillOfferIds = skillOffersDto.stream()
                .map(SkillOfferDto::getSkillId).toList();

        receiverSkills.stream()
                .filter(skill -> skillOfferIds.contains(skill.getId()))
                .forEach(matchedSkill -> {
                    if (!validator.checkIsGuarantor(matchedSkill, participants.author.getId())) {
                        addGuarantor(matchedSkill, participants);
                    }
                });

        skillOffersDto.removeIf(offer -> skillOfferIds.contains(offer.getSkillId()));
    }

    private void addGuarantor(Skill skill, Participants participants) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(participants.receiver)
                .skill(skill)
                .guarantor(participants.author)
                .build();

        List<UserSkillGuarantee> guarantees = Optional.ofNullable(skill.getGuarantees())
                .orElseGet(ArrayList::new);

        guarantees.add(guarantee);
    }

    @Transactional
    public void saveNewSkills(Participants participants, List<SkillOfferDto> skillOffersDto) {
        List<Skill> newSkills = skillRepository.findAllById(
                skillOffersDto.stream()
                        .map(SkillOfferDto::getSkillId)
                        .collect(Collectors.toList()));

        newSkills.forEach(skill -> addGuarantor(skill, participants));
        participants.receiver.getSkills().addAll(newSkills);

        userRepository.save(participants.receiver);
    }

    @Transactional
    public void saveSkillOffers(List<SkillOfferDto> skillOffersDto) {
        skillOffersDto.forEach(offer -> skillOfferRepository.create(
                offer.getSkillId(),
                offer.getRecommendationId()));
    }
}
