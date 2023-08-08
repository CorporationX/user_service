package school.faang.user_service.util.google;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.GoogleTokenRepository;

@RequiredArgsConstructor
@Component
public class JpaDataStoreFactory implements DataStoreFactory {
    private final GoogleTokenRepository repository;

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        return (DataStore<V>) new JpaDataStore(this, id, repository);
    }
}
