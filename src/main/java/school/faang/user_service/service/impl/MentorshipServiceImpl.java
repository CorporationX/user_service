package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.users.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;

    @Override
    public List<UserDto> getMentees(long userId) {
        final User userById = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с userID %s не найден", userId)));
        return mentorshipMapper.toUserDto(userById.getMentees());
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        final User userById = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с userID %s не найден", userId)));
        return mentorshipMapper.toUserDto(userById.getMentors());
    }

    @Override
    @Transactional
    public void deleteMentee(long userId, long menteeId) {
        User mentor = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ментор с ID %s не найден", userId)));

        boolean removed = mentor.getMentees().removeIf(mentee -> mentee.getId() == menteeId);

        if (!removed) {
            throw new EntityNotFoundException(
                    String.format("Менти с ID %s не найден у ментора с ID %s", menteeId, userId));
        }

        mentorshipRepository.save(mentor);
    }

    @Override
    @Transactional
    public void deleteMentor(long userId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ментор с ID %s не найден", mentorId)));

        boolean removed = mentor.getMentees().removeIf(mentee -> mentee.getId() == userId);

        if (!removed) {
            throw new EntityNotFoundException(
                    String.format("Менти с ID %s не найден у ментора с ID %s", userId, mentorId));
        }

        mentorshipRepository.save(mentor);
    }
}
