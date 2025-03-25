package school.faang.user_service.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipDeleteDto;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.MentorshipRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private static final Long DEFAULT_MENTOR_ID = 1L;
    private static final Long DEFAULT_MENTEE_ID = 2L;
    private static final Long ANOTHER_MENTOR_ID = 3L;

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipServiceImpl mentorshipService;

    @Nested
    class TestGetMentees {

        @Test
        void testGetMentees_shouldReturnDtoList_whenMenteesExist() {
            // given
            User mentee = new User();
            mentee.setId(DEFAULT_MENTEE_ID);
            mentee.setUsername("menteeUser");
            mentee.setEmail("mentee@example.com");

            List<User> mentees = List.of(mentee);
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(mentees);

            UserDto menteeDto = new UserDto(mentee.getId(), mentee.getUsername(), mentee.getEmail());
            when(userMapper.toDto(mentee)).thenReturn(menteeDto);

            // when
            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(menteeDto, result.get(0));

            // verify
            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(DEFAULT_MENTOR_ID);
            verify(userMapper, times(1)).toDto(mentee);
            verifyNoMoreInteractions(mentorshipRepository, userMapper);
        }

        @Test
        void testGetMentees_shouldReturnEmptyList_whenNoMenteesExist() {
            // given
            when(mentorshipRepository.findAllMenteesByMentorId(DEFAULT_MENTOR_ID)).thenReturn(List.of());

            // when
            List<UserDto> result = mentorshipService.getMentees(DEFAULT_MENTOR_ID);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // verify
            verify(mentorshipRepository, times(1)).findAllMenteesByMentorId(DEFAULT_MENTOR_ID);
            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(mentorshipRepository);
        }
    }

    @Nested
    class TestGetMentors {

        @Test
        void testGetMentors_shouldReturnDtoList_whenMentorsExist() {
            // given
            User mentor = new User();
            mentor.setId(ANOTHER_MENTOR_ID);
            mentor.setUsername("mentorUser");
            mentor.setEmail("mentor@example.com");

            List<User> mentors = List.of(mentor);
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(mentors);

            UserDto mentorDto = new UserDto(mentor.getId(), mentor.getUsername(), mentor.getEmail());
            when(userMapper.toDto(mentor)).thenReturn(mentorDto);

            // when
            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(mentorDto, result.get(0));

            // verify
            verify(mentorshipRepository, times(1)).findAllMentorsByMenteeId(DEFAULT_MENTEE_ID);
            verify(userMapper, times(1)).toDto(mentor);
            verifyNoMoreInteractions(mentorshipRepository, userMapper);
        }

        @Test
        void testGetMentors_shouldReturnEmptyList_whenNoMentorsExist() {
            // given
            when(mentorshipRepository.findAllMentorsByMenteeId(DEFAULT_MENTEE_ID)).thenReturn(List.of());

            // when
            List<UserDto> result = mentorshipService.getMentors(DEFAULT_MENTEE_ID);

            // then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // verify
            verify(mentorshipRepository, times(1)).findAllMentorsByMenteeId(DEFAULT_MENTEE_ID);
            verifyNoInteractions(userMapper);
            verifyNoMoreInteractions(mentorshipRepository);
        }
    }

    @Nested
    class TestDeleteMentee {

        @Test
        void testDeleteMentee_shouldRemoveRelationship_whenExists() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            long mentorshipId = 10L;
            MentorshipDeleteDto dto = new MentorshipDeleteDto(mentorId, menteeId);

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.of(mentorshipId));
            // Act
            SuccessResponseDto response = mentorshipService.deleteMentee(dto);

            // Assert
            verify(mentorshipRepository).deleteById(mentorshipId);
            assertEquals("Mentee with ID 2 successfully deleted from mentor with ID 1", response.message());
        }

        @Test
        void testDeleteMentee_shouldThrowException_whenRelationshipNotFound() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            MentorshipDeleteDto dto = new MentorshipDeleteDto(mentorId, menteeId);

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MentorshipNotFoundException.class, () -> mentorshipService.deleteMentee(dto));
        }
    }

    @Nested
    class TestDeleteMentor {

        @Test
        void testDeleteMentor_shouldRemoveRelationship_whenExists() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            long mentorshipId = 10L;
            MentorshipDeleteDto dto = new MentorshipDeleteDto(mentorId, menteeId);

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.of(mentorshipId));

            // Act
            SuccessResponseDto response = mentorshipService.deleteMentor(dto);

            // Assert
            verify(mentorshipRepository).deleteById(mentorshipId);
            assertEquals("Mentor with ID 1 successfully deleted from mentee with ID 2", response.message());
        }

        @Test
        void testDeleteMentor_shouldThrowException_whenMenteeNotFound() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;
            MentorshipDeleteDto dto = new MentorshipDeleteDto(mentorId, menteeId);

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(MentorshipNotFoundException.class, () -> mentorshipService.deleteMentor(dto));
        }

        @Test
        void testFindMentorshipConnectionId_shouldThrowException_whenRelationshipNotFound() {
            // Arrange
            long mentorId = 1L;
            long menteeId = 2L;

            when(mentorshipRepository.findIdByMentorIdAndMenteeId(mentorId, menteeId))
                    .thenReturn(Optional.empty());

            // Act
            MentorshipNotFoundException exception = assertThrows(
                    MentorshipNotFoundException.class,
                    () -> mentorshipService.findMentorshipConnectionId(mentorId, menteeId)
            );

            // Assert
            assertEquals(
                    "No mentorship relationship found for mentor 1 and mentee 2",
                    exception.getMessage()
            );
        }
    }
}
