package com.demo.kafka.service.mapper;

import com.demo.kafka.domain.Producer;
import com.demo.kafka.service.dto.ProducerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Producer} and its DTO {@link ProducerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProducerMapper extends EntityMapper<ProducerDTO, Producer> {}
