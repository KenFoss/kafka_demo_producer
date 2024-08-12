package com.demo.kafkaProducer.web.rest;

import static com.demo.kafkaProducer.domain.ProducerAsserts.*;
import static com.demo.kafkaProducer.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.demo.kafkaProducer.IntegrationTest;
import com.demo.kafkaProducer.domain.Producer;
import com.demo.kafkaProducer.repository.ProducerRepository;
import com.demo.kafkaProducer.service.dto.ProducerDTO;
import com.demo.kafkaProducer.service.mapper.ProducerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProducerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AvroProducerResourceIT {

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/producers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProducerRepository producerRepository;

    @Autowired
    private ProducerMapper producerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProducerMockMvc;

    private Producer producer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producer createEntity(EntityManager em) {
        Producer producer = new Producer().ownerName(DEFAULT_OWNER_NAME).productName(DEFAULT_PRODUCT_NAME).quantity(DEFAULT_QUANTITY);
        return producer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Producer createUpdatedEntity(EntityManager em) {
        Producer producer = new Producer().ownerName(UPDATED_OWNER_NAME).productName(UPDATED_PRODUCT_NAME).quantity(UPDATED_QUANTITY);
        return producer;
    }

    @BeforeEach
    public void initTest() {
        producer = createEntity(em);
    }

    @Test
    @Transactional
    void createProducer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);
        var returnedProducerDTO = om.readValue(
            restProducerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProducerDTO.class
        );

        // Validate the Producer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProducer = producerMapper.toEntity(returnedProducerDTO);
        assertProducerUpdatableFieldsEquals(returnedProducer, getPersistedProducer(returnedProducer));
    }

    @Test
    @Transactional
    void createProducerWithExistingId() throws Exception {
        // Create the Producer with an existing ID
        producer.setId(1L);
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProducerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProducers() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get all the producerList
        restProducerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(producer.getId().intValue())))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME)))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getProducer() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        // Get the producer
        restProducerMockMvc
            .perform(get(ENTITY_API_URL_ID, producer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(producer.getId().intValue()))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingProducer() throws Exception {
        // Get the producer
        restProducerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProducer() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producer
        Producer updatedProducer = producerRepository.findById(producer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProducer are not directly saved in db
        em.detach(updatedProducer);
        updatedProducer.ownerName(UPDATED_OWNER_NAME).productName(UPDATED_PRODUCT_NAME).quantity(UPDATED_QUANTITY);
        ProducerDTO producerDTO = producerMapper.toDto(updatedProducer);

        restProducerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, producerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(producerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProducerToMatchAllProperties(updatedProducer);
    }

    @Test
    @Transactional
    void putNonExistingProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, producerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(producerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(producerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(producerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProducerWithPatch() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producer using partial update
        Producer partialUpdatedProducer = new Producer();
        partialUpdatedProducer.setId(producer.getId());

        partialUpdatedProducer.ownerName(UPDATED_OWNER_NAME).quantity(UPDATED_QUANTITY);

        restProducerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducer))
            )
            .andExpect(status().isOk());

        // Validate the Producer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProducerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProducer, producer), getPersistedProducer(producer));
    }

    @Test
    @Transactional
    void fullUpdateProducerWithPatch() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the producer using partial update
        Producer partialUpdatedProducer = new Producer();
        partialUpdatedProducer.setId(producer.getId());

        partialUpdatedProducer.ownerName(UPDATED_OWNER_NAME).productName(UPDATED_PRODUCT_NAME).quantity(UPDATED_QUANTITY);

        restProducerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProducer))
            )
            .andExpect(status().isOk());

        // Validate the Producer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProducerUpdatableFieldsEquals(partialUpdatedProducer, getPersistedProducer(partialUpdatedProducer));
    }

    @Test
    @Transactional
    void patchNonExistingProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, producerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(producerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(producerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        producer.setId(longCount.incrementAndGet());

        // Create the Producer
        ProducerDTO producerDTO = producerMapper.toDto(producer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProducerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(producerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Producer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducer() throws Exception {
        // Initialize the database
        producerRepository.saveAndFlush(producer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the producer
        restProducerMockMvc
            .perform(delete(ENTITY_API_URL_ID, producer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return producerRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Producer getPersistedProducer(Producer producer) {
        return producerRepository.findById(producer.getId()).orElseThrow();
    }

    protected void assertPersistedProducerToMatchAllProperties(Producer expectedProducer) {
        assertProducerAllPropertiesEquals(expectedProducer, getPersistedProducer(expectedProducer));
    }

    protected void assertPersistedProducerToMatchUpdatableProperties(Producer expectedProducer) {
        assertProducerAllUpdatablePropertiesEquals(expectedProducer, getPersistedProducer(expectedProducer));
    }
}
