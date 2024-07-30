package com.demo.kafka.repository;

import com.demo.kafka.domain.Producer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Producer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {}
