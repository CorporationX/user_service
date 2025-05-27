package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.adapter.CareerRepositoryAdapter;
import school.faang.user_service.entity.Career;
import school.faang.user_service.repository.CareerRepository;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerRepositoryAdapterTest {

    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private CareerRepositoryAdapter careerRepositoryAdapter;

    @Test
    void testGetCareerById_Success() {
        long careerId = 1L;
        Career expectedCareer = new Career();
        expectedCareer.toBuilder().id(careerId).build();

        when(careerRepository.findById(careerId)).thenReturn(Optional.of(expectedCareer));

        Career result = careerRepositoryAdapter.getCareerById(careerId);

        assertNotNull(result);
        assertEquals(expectedCareer, result);
        verify(careerRepository).findById(careerId);
    }

    @Test
    void testGetCareerById_NotFound() {
        long careerId = 1L;

        when(careerRepository.findById(careerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> careerRepositoryAdapter.getCareerById(careerId));

        verify(careerRepository).findById(careerId);
    }
}
