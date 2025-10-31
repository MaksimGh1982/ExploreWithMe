package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main.common.EventState;
import ru.practicum.main.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findByInitiatorId(Long id);

    Event findByIdAndInitiatorId(Long eventId, Long userId);

    Event findByIdAndState(Long eventId, EventState state);

    List<Event> findByState(EventState state);

    @Query("select u from Event u where (upper(u.annotation) like upper(?1) or upper(u.description) like upper(?2)) and u.state=\"PUBLISHED\"")
    List<Event> findByText(String annotationSearch, String descriptionSearch);

}
