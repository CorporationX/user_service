package school.faang.user_service.controller.mentorship;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.mentorship.MentorshipService;

@TestInstance(Lifecycle.PER_CLASS)
class MentorshipControllerTest {

  private static final long DEFAULT_ID = 1L;
  private static final long ANOTHER_ID = 2L;
  private static final String RECORD_DELETED = "Запись была удалена!";

  private AutoCloseable mocks;
  private MentorshipDto resultDto;
  private ResponseEntity responseEntity;

  @Mock
  private MentorshipService mentorshipService;

  @InjectMocks
  private MentorshipController mentorshipController;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);

    resultDto = MentorshipDto.builder()
        .id(DEFAULT_ID)
        .build();

    when(mentorshipService.getMentees(DEFAULT_ID))
        .thenReturn(List.of(resultDto));
    when(mentorshipService.getMentors(DEFAULT_ID))
        .thenReturn(List.of(resultDto));
    when(mentorshipService.getDeletionResultMentorship(DEFAULT_ID, DEFAULT_ID))
        .thenReturn(Boolean.TRUE);
  }

  @Test
  @DisplayName("Проверка ответа с статусом 200, типом сообщения и тела сообщения если менти был найден.")
  void testGetMenteesReturnsValidResponseEntity() {
    responseEntity = mentorshipController.getMentees(DEFAULT_ID);
    validResponseEntity();
    assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    assertEquals(List.of(resultDto), responseEntity.getBody());
  }

  @Test
  @DisplayName("Проверка ответа с статусом 200, типом сообщения и тела сообщения если ментор был найден.")
  void testGetMentorsReturnsValidResponseEntity() {
    responseEntity = mentorshipController.getMentors(DEFAULT_ID);
    validResponseEntity();
    assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
    assertEquals(List.of(resultDto), responseEntity.getBody());
  }

  @Test
  @DisplayName("Проверка ответа с статусом 404, если ни один менти не был найден.")
  void testGetMenteesReturnsNotValidResponseEntity() {
    responseEntity = mentorshipController.getMentees(ANOTHER_ID);
    notValidResponseEntity();
  }

  @Test
  @DisplayName("Проверка ответа с статусом 404, если ни один ментор не был найден.")
  void testGetMentorsReturnsNotValidResponseEntity() {
    responseEntity = mentorshipController.getMentors(ANOTHER_ID);
    notValidResponseEntity();
  }

  @Test
  @DisplayName("Проверка ответа с статусом 200, типом сообщения и тела сообщения если менти был удален.")
  void testDeleteMenteeValidResponseEntity() {
    responseEntity = mentorshipController.deleteMentee(DEFAULT_ID, DEFAULT_ID);
    validResponseEntity();
    assertEquals(MediaType.valueOf(MediaType.TEXT_HTML_VALUE), responseEntity.getHeaders().getContentType());
    assertEquals(RECORD_DELETED, responseEntity.getBody());
  }

  @Test
  @DisplayName("Проверка ответа с статусом 200, типом сообщения и тела сообщения если ментор был удален.")
  void testDeleteMentorValidResponseEntity() {
    responseEntity = mentorshipController.deleteMentor(DEFAULT_ID, DEFAULT_ID);
    validResponseEntity();
    assertEquals(MediaType.valueOf(MediaType.TEXT_HTML_VALUE), responseEntity.getHeaders().getContentType());
    assertEquals(RECORD_DELETED, responseEntity.getBody());
  }

  @Test
  @DisplayName("Проверка ответа с статусом 404, если нет менти для удаления.")
  void testDeleteMenteeNotValidResponseEntity() {
    responseEntity = mentorshipController.deleteMentee(ANOTHER_ID, ANOTHER_ID);
    notValidResponseEntity();
  }

  @Test
  @DisplayName("Проверка ответа с статусом 404, если нет ментора для удаления.")
  void testDeleteMentorNotValidResponseEntity() {
    responseEntity = mentorshipController.deleteMentor(ANOTHER_ID, ANOTHER_ID);
    notValidResponseEntity();
  }

  private void validResponseEntity() {
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  private void notValidResponseEntity() {
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

}
