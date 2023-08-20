package school.faang.user_service.util;


import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.repository.google.GoogleTokenRepository;

import java.io.Serializable;

@RequiredArgsConstructor
public class JpaDataStoreFactory implements DataStoreFactory {
    private final GoogleTokenRepository repository;

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        return (DataStore<V>) new JpaDataStore(this, id, repository);
    }
}