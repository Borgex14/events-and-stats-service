package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT h.app, h.uri, COUNT(h.id) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> findAllWithUrisFalse(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT h.app, h.uri, COUNT(h.id) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> findAllWithUrisTrue(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    @Query("SELECT h.app, h.uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> findAllWithUrisFalseUnique(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT h.app, h.uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<Object[]> findAllWithUrisTrueUnique(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris);

}