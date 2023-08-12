package school.faang.user_service.util;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.repository.GoogleTokenRepository;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaDataStore<V extends Serializable> extends AbstractDataStore<StoredCredential> {
    private final GoogleTokenRepository repository;

    protected JpaDataStore(JpaDataStoreFactory dataStoreFactory, String id, GoogleTokenRepository repository) {
        super(dataStoreFactory, id);
        this.repository = repository;
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<>(repository.findAllOauthClientId());
    }

    @Override
    public Collection<StoredCredential> values() throws IOException {
        return repository.findAll().stream()
                .map(GoogleToken::toStoredCredential)
                .collect(Collectors.toList());
    }

    @Override
    public StoredCredential get(String key) {
        GoogleToken googleToken = repository.findByOauthClientId(key);
        if (googleToken == null) {
            return null;
        }
        return googleToken.toStoredCredential();
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        return null;
    }

    @Override
    public DataStore<StoredCredential> clear() throws IOException {
        repository.deleteAll();
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) throws IOException {
        GoogleToken googleCredential = repository.findByOauthClientId(key);
        if (googleCredential == null) {
            throw new IllegalArgumentException("No credential found for clientId " + key);
        }
        repository.delete(googleCredential);
        return this;
    }
}
