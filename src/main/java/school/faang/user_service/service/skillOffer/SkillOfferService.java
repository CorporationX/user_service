package school.faang.user_service.service.skillOffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public void checkForSkills(List<SkillOfferDto> skillOfferDtos) {
//        List<Long> skillOfferIds = skillOfferDtos.stream()
//                .map(SkillOfferDto::getId)
//                .toList();
//        log.info(skillOfferIds.toString());
////        List<SkillOffer> skillOffertest = StreamSupport
////                .stream(skillOfferRepository.findAll().spliterator(), false)
////                .toList();
//        List<SkillOffer> skillOffers = StreamSupport
//                .stream(skillOfferRepository.findAllById(skillOfferIds).spliterator(), false)
//                .toList();
//        log.info(skillOffers.toString());
//        if (skillOffers.size() != skillOfferIds.size()) {
//            log.error(ExceptionMessages.SKILL_NOT_FOUND);
//            throw new NoSuchElementException(ExceptionMessages.SKILL_NOT_FOUND);
//        }
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

    public void saveSkillOffers(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();

        List<Skill> existingSkills = skillRepository.findAllByUserId(receiver.getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();

        boolean isAuthorSkillGuarantee = userSkillGuaranteeRepository.existsById(author.getId());

        List<UserSkillGuarantee> listForGuaranteeRepository = new ArrayList<>();
        List<SkillOffer> listForOfferRepository = new ArrayList<>();

        for (SkillOffer skillOffer : skillOffers) {
            if (skillOffer.getSkill() != null
                    && existingSkills.contains(skillOffer.getSkill())
                    && !isAuthorSkillGuarantee) {
                listForGuaranteeRepository.add(UserSkillGuarantee.builder()
                        .user(receiver)
                        .skill(skillOffer.getSkill())
                        .guarantor(author)
                        .build());
            } else {
                listForOfferRepository.add(skillOffer);
            }
        }
        userSkillGuaranteeRepository.saveAll(listForGuaranteeRepository);
        skillOfferRepository.saveAll(listForOfferRepository);
    }
}
