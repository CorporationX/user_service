package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public SkillOffer getSkillOffer(Long id) {
        return skillOfferRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill offer not found"));
    }
}
