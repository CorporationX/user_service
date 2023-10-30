package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.UserRating;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRatingRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRatingService {

    private final UserRatingRepository userRatingRepository;

    @Value("${premium.rating_for_premium}")
    private long ratingForPremium;

    @Transactional
    public void addRatingForPremium(long userId) {
        UserRating userRating = userRatingRepository.findByUserId(userId)
                .orElse(UserRating.builder().id(userId).rating(0L).build());
        userRating.setRating(userRating.getRating() + ratingForPremium);
        userRatingRepository.save(userRating);
    }

    @Transactional
    public void depriveRatingEndPremium(long id) {
        UserRating userRating = userRatingRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("UserRating not found"));
        userRating.setRating(userRating.getRating() - ratingForPremium);
    }
}
