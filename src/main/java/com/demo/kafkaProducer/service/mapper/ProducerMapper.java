package com.demo.kafkaProducer.service.mapper;

import com.demo.kafkaProducer.domain.Producer;
import com.demo.kafkaProducer.service.dto.ProducerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Producer} and its DTO {@link ProducerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProducerMapper extends EntityMapper<ProducerDTO, Producer> {}
