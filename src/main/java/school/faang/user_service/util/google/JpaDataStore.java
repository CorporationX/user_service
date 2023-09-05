package school.faang.user_service.util.google;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.GoogleTokenRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JpaDataStore extends AbstractDataStore<StoredCredential> {
    private final GoogleTokenRepository repository;
    protected JpaDataStore(DataStoreFactory dataStoreFactory,
                           String id,
                           GoogleTokenRepository repository) {
        super(dataStoreFactory, id);
        this.repository = repository;
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<>(repository.findAllOauthClientId());
    }

    @Override
    public Collection<StoredCredential> values() {
        return repository.findAll().stream()
                .map(GoogleToken::toStoredCredential)
                .toList();
    }

    @Override
    public StoredCredential get(String key) {
        return repository.findByOauthClientId(key)
                .map(GoogleToken::toStoredCredential)
                .orElse(null);
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        GoogleToken token = repository.findByOauthClientId(key)
                .orElse(GoogleToken.builder()
                        .oauthClientId(key)
                        .user(User.builder().id(Long.parseLong(key)).build())
                        .build());

        token.setUuid(UUID.randomUUID().toString());
        token.setAccessToken(value.getAccessToken());
        token.setRefreshToken(value.getRefreshToken());
        token.setExpirationTimeMilliseconds(value.getExpirationTimeMilliseconds());
        token.setUpdatedAt(LocalDateTime.now());
        repository.save(token);

        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() {
        repository.deleteAll();
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) {
        repository.delete(repository.findByOauthClientId(key)
                .orElseThrow(() -> new EntityNotFoundException("No token found for " + key)));
        return this;
    }
}
