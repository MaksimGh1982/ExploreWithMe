package ru.practicum.main.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.Views;


public interface ViewsRepository extends JpaRepository<Views, Long> {

    Long countByIpAndEvent(String ip, Long id);
}
