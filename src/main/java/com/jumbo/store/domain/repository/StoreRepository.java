package com.jumbo.store.domain.repository;

import com.jumbo.store.domain.model.Store;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByUuid(String uuid);

    boolean existsByUuid(String uuid);

    /**
     * Find all UUIDs that exist in the database from the provided list.
     * This is more efficient than checking existence one by one.
     */
    @Query("SELECT s.uuid FROM Store s WHERE s.uuid IN :uuids")
    Set<String> findExistingUuids(@Param("uuids") List<String> uuids);
}
