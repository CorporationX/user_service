package school.faang.user_service.requestformentoring.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.requestformentoring.helper.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.requestformentoring.helper.validation.ValidationDb;
import school.faang.user_service.requestformentoring.helper.validation.ValidationUser;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    private static final int MONTHS_REQUEST_MENTORSHIP_REQUEST = 3;
    @InjectMocks
    MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestRepository menReqRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidationDb validationDb;
    @Mock
    private ValidationUser validationUser;
    @Mock
    private MentorshipRequestMapper menReqMapper;


}