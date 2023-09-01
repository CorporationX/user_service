package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;
import school.faang.user_service.service.contact.ContactService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ContactService contactService;
    private final ContactPreferenceService contactPreferenceService;
    @Value("${telegram.botUrl}")
    private String botUrl;

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<UserDto> users = new ArrayList<>();

        userRepository.findAllById(ids)
                .forEach(user -> users.add(userMapper.toDto(user)));
        log.info("Users have taken from DB successfully");
        return users;
    }

    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("This user was not found"));
        log.info("User with id={} has taken successfully from DB", userId);
        return userMapper.toDto(user);
    }

    public String setTelegramContact() {
        long userId = userContext.getUserId();
        return botUrl + userId;
    }

    @Transactional
    public void saveTelegramChatId(long userId, long chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", userId)));
        ContactPreferenceDto contactPreference = ContactPreferenceDto.builder()
                .user(user)
                .preference(PreferredContact.TELEGRAM)
                .build();
        contactPreferenceService.save(contactPreference);
        ContactDto contact = ContactDto.builder()
                .user(user)
                .contact(String.valueOf(chatId))
                .type(ContactType.TELEGRAM)
                .build();
        contactService.save(contact);
        log.info("Telegram contact for user with id={} was saved successfully to DB", userId);
    }
}
