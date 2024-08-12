package com.demo.kafkaProducer.service;

import com.demo.kafkaProducer.domain.Producer;
import com.demo.kafkaProducer.repository.ProducerRepository;
import com.demo.kafkaProducer.service.dto.ProducerDTO;
import com.demo.kafkaProducer.service.mapper.ProducerMapper;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.demo.kafkaProducer.domain.Producer}.
 */
@Service
@Transactional
public class ProducerService {

    private final Logger log = LoggerFactory.getLogger(ProducerService.class);

    private final ProducerRepository producerRepository;

    private final ProducerMapper producerMapper;

    private final KafkaProducerService kafkaProducerService;

    public ProducerService(ProducerRepository producerRepository, ProducerMapper producerMapper, KafkaProducerService kafkaProducerService) {
        this.producerRepository = producerRepository;
        this.producerMapper = producerMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    /**
     * Save a producer.
     *
     * @param producerDTO the entity to save.
     * @return the persisted entity.
     */
    public ProducerDTO save(ProducerDTO producerDTO) {
        log.debug("Request to save Producer : {}", producerDTO);
        Producer producer = producerMapper.toEntity(producerDTO);
        producer = producerRepository.save(producer);

//        AvroProducer avroProducer
//        kafkaProducerService.sendMessage(producer);
        return producerMapper.toDto(producer);
    }

    /**
     * Update a producer.
     *
     * @param producerDTO the entity to save.
     * @return the persisted entity.
     */
    public ProducerDTO update(ProducerDTO producerDTO) {
        log.debug("Request to update Producer : {}", producerDTO);
        Producer producer = producerMapper.toEntity(producerDTO);
        producer = producerRepository.save(producer);
//        kafkaProducerService.sendMessage(producer);
        return producerMapper.toDto(producer);
    }

    /**
     * Partially update a producer.
     *
     * @param producerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProducerDTO> partialUpdate(ProducerDTO producerDTO) {
        log.debug("Request to partially update Producer : {}", producerDTO);

        return producerRepository
            .findById(producerDTO.getId())
            .map(existingProducer -> {
                producerMapper.partialUpdate(existingProducer, producerDTO);

                return existingProducer;
            })
            .map(producerRepository::save)
            .map(producerMapper::toDto);
    }

    /**
     * Get all the producers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProducerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Producers");
        return producerRepository.findAll(pageable).map(producerMapper::toDto);
    }

    /**
     * Get one producer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProducerDTO> findOne(Long id) {
        log.debug("Request to get Producer : {}", id);
        return producerRepository.findById(id).map(producerMapper::toDto);
    }

    /**
     * Delete the producer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Producer : {}", id);
        producerRepository.deleteById(id);
    }
}
