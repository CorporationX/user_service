//package school.faang.user_service.service.mentorship;
//
//
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.mentorship.GetMenteesResponse;
//import school.faang.user_service.dto.mentorship.GetMentorsResponse;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.mapper.mentorship.MenteeMapperImpl;
//import school.faang.user_service.mapper.mentorship.MentorsMapperImpl;
//import school.faang.user_service.repository.mentorship.MentorshipRepository;
//import school.faang.user_service.service.mentorship.MentorshipService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//
//@ExtendWith(MockitoExtension.class)
//public class MentorshipServiceTest {
//
//    @Mock
//    private MentorshipRepository mentorshipRepository;
//
//    @Spy
//    private MenteeMapperImpl menteeMapperImpl;
//
//    @Spy
//    private MentorsMapperImpl mentorsMapperImpl;
//
//    @InjectMocks
//    private MentorshipService mentorshipService;
//
//    @Test
//    public void testGetMentees() {
//        long id = 1L;
//        User mentor = new User();
//        mentor.setId(id);
//        mentor.setUsername("Miras");
//        User mentee = new User();
//        mentee.setId(2L);
//        mentee.setUsername("Jon");
//        List<User> mentees = new ArrayList<>();
//        mentees.add(mentee);
//        mentor.setMentees(mentees);
//        List<GetMenteesResponse> menteeList = mentees.stream()
//                .map(user -> menteeMapperImpl.toDto(user)).toList();
//        when(mentorshipRepository.findById(id))
//                .thenReturn(Optional.of(mentor));
//
//        mentorshipService.getMentees(id);
//
//        assertNotNull(mentorshipService.getMentees(id));
//        assertEquals(menteeList, mentorshipService.getMentees(id));
//    }
//
//    @Test
//    public void testGetMenteesNotFound() {
//        assertThrows(
//                EntityNotFoundException.class,
//                () -> mentorshipService.getMentees(1L)
//        );
//    }
//
//    @Test
//    public void testGetMentors() {
//        long id = 1L;
//        User mentor = new User();
//        mentor.setId(id);
//        mentor.setUsername("Miras");
//        User mentee = new User();
//        mentee.setId(2L);
//        mentee.setUsername("Jon");
//
//        List<User> mentors = new ArrayList<>();
//        mentors.add(mentor);
//        mentee.setMentors(mentors);
//
//        List<GetMentorsResponse> mentorList = mentors.stream()
//                .map(user -> mentorsMapperImpl.toDto(user)).toList();
//
//        when(mentorshipRepository.findById(id))
//                .thenReturn(Optional.of(mentee));
//
//        mentorshipService.getMentors(id);
//
//        assertNotNull(mentorshipService.getMentors(id));
//        assertEquals(mentorList, mentorshipService.getMentors(id));
//    }
//
////    @Test
////    public void testDeleteMentee
//}
