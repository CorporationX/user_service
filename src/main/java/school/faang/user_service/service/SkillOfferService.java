package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.EventSkillOfferedDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.skill.EventSkillOfferedMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillOfferedEventValidator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferedEventValidator validator;
    private final EventSkillOfferedMapper eventSkillOfferedMapper;

    public SkillOffer getSkillOffer(Long id) {
        return skillOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill offer not found"));
    }

    public EventSkillOfferedDto createSkillOffer(EventSkillOfferedDto dto) {
        validator.validate(dto);
        SkillOffer entity = eventSkillOfferedMapper.toEntity(dto);

        return eventSkillOfferedMapper.toDto(skillOfferRepository.save(entity));
    }

    public List<SkillOffer> findAllOffersOfSkill(long skillId, long userId) {
        return skillOfferRepository.findAllOffersOfSkill(skillId, userId);
    }

    public List<SkillOffer> findAllByUserId(long userId) {
        return skillOfferRepository.findAllByUserId(userId);
    }
}