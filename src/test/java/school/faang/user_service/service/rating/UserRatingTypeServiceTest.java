package school.faang.user_service.service.rating;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.repository.rating.UserRatingTypeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRatingTypeServiceTest {

    @Mock
    private UserRatingTypeRepository userRatingTypeRepository;

    @InjectMocks
    private UserRatingTypeService userRatingTypeService;

    private UserRatingType userRatingType;

    @BeforeEach
    void setUp() {
        userRatingType = new UserRatingType();
        userRatingType.setName(RatingType.GOAL_RATING);
        userRatingType.setCost(5.0);
    }

    @Test
    void findByName() {
        when(userRatingTypeRepository.findByName(RatingType.GOAL_RATING)).thenReturn(userRatingType);

        UserRatingType result = userRatingTypeService.findByName(RatingType.GOAL_RATING);

        assertEquals(userRatingType, result);
        verify(userRatingTypeRepository, times(1)).findByName(RatingType.GOAL_RATING);
    }

    @Test
    void save() {
        when(userRatingTypeRepository.save(userRatingType)).thenReturn(userRatingType);

        UserRatingType result = userRatingTypeService.save(userRatingType);

        assertEquals(userRatingType, result);
        verify(userRatingTypeRepository, times(1)).save(userRatingType);
    }

    @Test
    void findById() {
        when(userRatingTypeRepository.findById(1L)).thenReturn(Optional.of(userRatingType));

        UserRatingType result = userRatingTypeService.findById(1L);

        assertEquals(userRatingType, result);
        verify(userRatingTypeRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(userRatingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        UserRatingType result = userRatingTypeService.findById(1L);

        assertNull(result);
        verify(userRatingTypeRepository, times(1)).findById(1L);
    }

    @Test
    void delete() {
        doNothing().when(userRatingTypeRepository).delete(userRatingType);

        userRatingTypeService.delete(userRatingType);

        verify(userRatingTypeRepository, times(1)).delete(userRatingType);
    }
}