package com.creditsuisse.recruitment.repositories;

import com.creditsuisse.recruitment.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
}
