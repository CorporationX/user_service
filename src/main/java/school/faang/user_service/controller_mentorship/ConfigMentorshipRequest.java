package school.faang.user_service.controller_mentorship;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper_mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service_mentorship.MentorshipRequestService;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Configuration
public class ConfigMentorshipRequest {

    @Bean
    public MentorshipRequestController mentorshipRequestController(MentorshipRequestService mentorshipRequestService) {
        return new MentorshipRequestController(mentorshipRequestService);
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository() {
            @Override
            public int countOwnedSkills(long userId, List<Long> ids) {
                return 0;
            }

            @Override
            public Stream<User> findPremiumUsers() {
                return Stream.empty();
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends User> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public void deleteAllInBatch(Iterable<User> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public User getOne(Long aLong) {
                return null;
            }

            @Override
            public User getById(Long aLong) {
                return null;
            }

            @Override
            public User getReferenceById(Long aLong) {
                return null;
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example) {
                return List.of();
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
                return List.of();
            }

            @Override
            public <S extends User> List<S> saveAll(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public List<User> findAll() {
                return List.of();
            }

            @Override
            public List<User> findAllById(Iterable<Long> longs) {
                return List.of();
            }

            @Override
            public <S extends User> S save(S entity) {
                return null;
            }

            @Override
            public Optional<User> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(User entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends User> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public List<User> findAll(Sort sort) {
                return List.of();
            }

            @Override
            public Page<User> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends User> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }
        };
    }

    @Bean
    public MentorshipRequestService mentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository,
                                                             UserRepository userRepository,
                                                             MentorshipRequestMapper mentorshipRequestMapper) {
        return new MentorshipRequestService(mentorshipRequestRepository, userRepository, mentorshipRequestMapper);
    }

    @Bean
    public MentorshipRequestRepository mentorshipRequestRepository() {
        return new MentorshipRequestRepository() {

            @Override
            public List<MentorshipRequest> findAll(Sort sort) {
                return List.of();
            }

            @Override
            public Page<MentorshipRequest> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends MentorshipRequest> S save(S entity) {
                return null;
            }

            @Override
            public <S extends MentorshipRequest> List<S> saveAll(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public Optional<MentorshipRequest> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public List<MentorshipRequest> findAll() {
                return List.of();
            }

            @Override
            public List<MentorshipRequest> findAllById(Iterable<Long> longs) {
                return List.of();
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(MentorshipRequest entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends MentorshipRequest> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends MentorshipRequest> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends MentorshipRequest> List<S> saveAllAndFlush(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public void deleteAllInBatch(Iterable<MentorshipRequest> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public MentorshipRequest getOne(Long aLong) {
                return null;
            }

            @Override
            public MentorshipRequest getById(Long aLong) {
                return null;
            }

            @Override
            public MentorshipRequest getReferenceById(Long aLong) {
                return null;
            }

            @Override
            public <S extends MentorshipRequest> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends MentorshipRequest> List<S> findAll(Example<S> example) {
                return List.of();
            }

            @Override
            public <S extends MentorshipRequest> List<S> findAll(Example<S> example, Sort sort) {
                return List.of();
            }

            @Override
            public <S extends MentorshipRequest> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends MentorshipRequest> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends MentorshipRequest> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends MentorshipRequest, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }

            @Override
            public MentorshipRequest create(long requesterId, long receiverId, String description) {
                return null;
            }

            @Override
            public Optional<MentorshipRequest> findLatestRequest(long requesterId, long receiverId) {
                return Optional.empty();
            }

            @Override
            public boolean existAcceptedRequest(long requesterId, long receiverId) {
                return false;
            }

            @Override
            public List<MentorshipRequest> findAllByRequesterId(long requesterId) {
                return List.of();
            }
        };
    }

}
