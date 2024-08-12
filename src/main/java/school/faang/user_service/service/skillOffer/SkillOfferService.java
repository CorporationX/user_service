package school.faang.user_service.service.skillOffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.UserSkillGuaranteeDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.mapper.recommendation.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final UserService userService;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserSkillGuaranteeMapper userSkillGuaranteeMapper;
    private final SkillOfferMapper skillOfferMapper;

    public void checkForSkills(List<SkillOfferDto> skillOfferDtos) {
        if (skillOfferDtos == null || skillOfferDtos.isEmpty()) {
            return;
        }

        for (var skillOffer : skillOfferDtos) {
            if (!skillRepository.existsById(skillOffer.getSkillId())) {
                String errorMessage = String.format("The skill (ID : %d) doesn't exists in the system", skillOffer.getSkillId());
                log.error(errorMessage);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    public void updateSkillGuarantee(List<SkillOfferDto> skillOfferDtoList, long authorId, long receiverId) {
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return;
        }

        List<Long> skillOfferIds = skillOfferDtoList.stream()
                .map(SkillOfferDto::getSkillId).toList();
        List<Long> userSkillIds = userService.getUserSkillsId(receiverId);
        List<UserSkillGuaranteeDto> skillGuaranteeDtoList = new ArrayList<>();

        for (var skillOfferId : skillOfferIds) {
            if (userSkillIds.contains(skillOfferId) &&
                    !userSkillGuaranteeRepository.existsUserSkillGuaranteeByUserIdAndGuarantorIdAndSkillId(receiverId, authorId, skillOfferId)) {
                skillGuaranteeDtoList.add(UserSkillGuaranteeDto.builder()
                        .userId(receiverId)
                        .guarantorId(authorId)
                        .skillId(skillOfferId).build()
                );
            }
        }
        userSkillGuaranteeRepository.saveAll(userSkillGuaranteeMapper.toEntityList(skillGuaranteeDtoList));
    }

    public List<SkillOfferDto> newSkillOffersToUpdate(List<SkillOfferDto> skillOfferDto, List<SkillOfferDto> updateSkillOffer) {
        List<Long> skillOfferIds = skillOfferDto.stream().map(SkillOfferDto::getSkillId).toList();
        List<SkillOfferDto> result = new ArrayList<>();

        for (var skillOffer : updateSkillOffer) {
            if (!skillOfferIds.contains(skillOffer.getSkillId())) {
                result.add(SkillOfferDto.builder().skillId(skillOffer.getSkillId()).build());
            }
        }
        return result;
    }

    public List<SkillOffer> saveSkillOffers(List<SkillOfferDto> skillOfferDtoList, long recommendationId) {
        if (skillOfferDtoList == null || skillOfferDtoList.isEmpty()) {
            return Collections.emptyList();
        }
        skillOfferDtoList.forEach(skillOfferDto -> skillOfferDto.setRecommendationId(recommendationId));
        List<SkillOffer> skillOffers = skillOfferMapper.toListOffersEntity(skillOfferDtoList);

        return skillOfferRepository.saveAll(skillOffers);
    }
}