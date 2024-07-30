package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.util.CollectionUtils;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@TestInstance(Lifecycle.PER_CLASS)
class MentorshipServiceTest {

  private static final long DEFAULT_ID = 1L;
  private static final long ANOTHER_ID = 2L;

  private User user;
  private MentorshipDto dto;

  @Mock
  private MentorshipRepository mentorshipRepository;

  @Spy
  private static MentorshipMapperImpl mentorshipMapper;

  @InjectMocks
  private MentorshipService mentorshipService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = User.builder()
        .id(DEFAULT_ID)
        .build();

    dto = MentorshipDto.builder()
        .id(DEFAULT_ID)
        .build();

    when(mentorshipRepository.getAllMenteesById(DEFAULT_ID))
        .thenReturn(List.of(user));
    when(mentorshipRepository.getAllMentorsById(DEFAULT_ID))
        .thenReturn(List.of(user));
    when(mentorshipRepository.deleteMentorshipById(DEFAULT_ID))
        .thenReturn(Math.toIntExact(DEFAULT_ID));
    when(mentorshipRepository.getMentorshipIdByMentorIdAndMenteeId(DEFAULT_ID, DEFAULT_ID))
        .thenReturn(DEFAULT_ID);
  }

  @Test
  @DisplayName("Проверка получения списка менти из БД по его id.")
  void testGetActualMentees() {
   List<MentorshipDto> resultList =  mentorshipService.getMentees(DEFAULT_ID);
   assertEquals(List.of(dto), resultList);
  }

  @Test
  @DisplayName("Проверка получения списка ментора из БД по его id.")
  void testGetEmptyMentors() {
    List<MentorshipDto> resultList =  mentorshipService.getMentors(DEFAULT_ID);
    assertEquals(List.of(dto), resultList);
  }

  @Test
  @DisplayName("Проверка получения пустого списка менти из БД, если его id не найден.")
  void testGetEmptyMentees() {
    List<MentorshipDto> resultList =  mentorshipService.getMentees(ANOTHER_ID);
    assertTrue(CollectionUtils.isEmpty(resultList));
  }

  @Test
  @DisplayName("Проверка получения пустого списка ментора из БД, если его id не найден.")
  void testGetActualMentors() {
    List<MentorshipDto> resultList =  mentorshipService.getMentors(ANOTHER_ID);
    assertTrue(CollectionUtils.isEmpty(resultList));
  }

  @Test
  @DisplayName("Проверка удаления менти/ментора если он был найден.")
  void testSuccessfulDeleteMentee() {
    assertTrue(mentorshipService.getDeletionResultMentorship(DEFAULT_ID, DEFAULT_ID));
  }

  @Test
  @DisplayName("Проверка удаления менти/ментора если он не был найден.")
  void testUnsuccessfulDeleteMentee() {
    assertFalse(mentorshipService.getDeletionResultMentorship(DEFAULT_ID, ANOTHER_ID));
  }

  @Test
  @DisplayName("Проверка выброса исключения при удалении ментора.")
  void testExceptionOnDeleteMentorship() {
    when(mentorshipRepository.deleteMentorshipById(ANOTHER_ID)).thenThrow();
    assertThrows(Exception.class, () -> mentorshipService.getDeletionResultMentorship(ANOTHER_ID, ANOTHER_ID));
  }

}
