package stats;

import dto.StatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long>, QuerydslPredicateExecutor<EndpointHit> {

    @Query("select new dto.StatsDto(u.app, u.uri, count(*)) from EndpointHit u " +
            "where u.timestamp BETWEEN :start AND :end " +
            "GROUP BY u.app, u.uri")
    List<StatsDto> notUniqueGetStatsDto(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("select new dto.StatsDto(u.app, u.uri, count(distinct ip)) from EndpointHit u " +
            "where u.timestamp BETWEEN :start AND :end " +
            "GROUP BY u.app, u.uri")
    List<StatsDto> uniqueGetStatsDto(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

}
