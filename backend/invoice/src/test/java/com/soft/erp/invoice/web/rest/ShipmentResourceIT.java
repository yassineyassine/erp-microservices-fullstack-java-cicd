package com.soft.erp.invoice.web.rest;

import static com.soft.erp.invoice.domain.ShipmentAsserts.*;
import static com.soft.erp.invoice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soft.erp.invoice.IntegrationTest;
import com.soft.erp.invoice.domain.Invoice;
import com.soft.erp.invoice.domain.Shipment;
import com.soft.erp.invoice.repository.EntityManager;
import com.soft.erp.invoice.repository.ShipmentRepository;
import com.soft.erp.invoice.service.ShipmentService;
import com.soft.erp.invoice.service.dto.ShipmentDTO;
import com.soft.erp.invoice.service.mapper.ShipmentMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ShipmentResourceIT {

    private static final String DEFAULT_TRACKING_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TRACKING_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentRepository shipmentRepositoryMock;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Mock
    private ShipmentService shipmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Shipment shipment;

    private Shipment insertedShipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createEntity(EntityManager em) {
        Shipment shipment = new Shipment().trackingCode(DEFAULT_TRACKING_CODE).date(DEFAULT_DATE).details(DEFAULT_DETAILS);
        // Add required entity
        Invoice invoice;
        invoice = em.insert(InvoiceResourceIT.createEntity()).block();
        shipment.setInvoice(invoice);
        return shipment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createUpdatedEntity(EntityManager em) {
        Shipment updatedShipment = new Shipment().trackingCode(UPDATED_TRACKING_CODE).date(UPDATED_DATE).details(UPDATED_DETAILS);
        // Add required entity
        Invoice invoice;
        invoice = em.insert(InvoiceResourceIT.createUpdatedEntity()).block();
        updatedShipment.setInvoice(invoice);
        return updatedShipment;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Shipment.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        InvoiceResourceIT.deleteEntities(em);
    }

    @BeforeEach
    void initTest() {
        shipment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedShipment != null) {
            shipmentRepository.delete(insertedShipment).block();
            insertedShipment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createShipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);
        var returnedShipmentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ShipmentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Shipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipment = shipmentMapper.toEntity(returnedShipmentDTO);
        assertShipmentUpdatableFieldsEquals(returnedShipment, getPersistedShipment(returnedShipment));

        insertedShipment = returnedShipment;
    }

    @Test
    void createShipmentWithExistingId() throws Exception {
        // Create the Shipment with an existing ID
        shipment.setId(1L);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setDate(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllShipments() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get all the shipmentList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(shipment.getId().intValue()))
            .jsonPath("$.[*].trackingCode")
            .value(hasItem(DEFAULT_TRACKING_CODE))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()))
            .jsonPath("$.[*].details")
            .value(hasItem(DEFAULT_DETAILS));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentsWithEagerRelationshipsIsEnabled() {
        when(shipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(shipmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllShipmentsWithEagerRelationshipsIsNotEnabled() {
        when(shipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(shipmentRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getShipment() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        // Get the shipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, shipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(shipment.getId().intValue()))
            .jsonPath("$.trackingCode")
            .value(is(DEFAULT_TRACKING_CODE))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()))
            .jsonPath("$.details")
            .value(is(DEFAULT_DETAILS));
    }

    @Test
    void getNonExistingShipment() {
        // Get the shipment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).block();
        updatedShipment.trackingCode(UPDATED_TRACKING_CODE).date(UPDATED_DATE).details(UPDATED_DETAILS);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(updatedShipment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentToMatchAllProperties(updatedShipment);
    }

    @Test
    void putNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment.trackingCode(UPDATED_TRACKING_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShipment, shipment), getPersistedShipment(shipment));
    }

    @Test
    void fullUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment.trackingCode(UPDATED_TRACKING_CODE).date(UPDATED_DATE).details(UPDATED_DETAILS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedShipment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(partialUpdatedShipment, getPersistedShipment(partialUpdatedShipment));
    }

    @Test
    void patchNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, shipmentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(shipmentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteShipment() {
        // Initialize the database
        insertedShipment = shipmentRepository.save(shipment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, shipment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentRepository.count().block();
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

    protected Shipment getPersistedShipment(Shipment shipment) {
        return shipmentRepository.findById(shipment.getId()).block();
    }

    protected void assertPersistedShipmentToMatchAllProperties(Shipment expectedShipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShipmentAllPropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
        assertShipmentUpdatableFieldsEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }

    protected void assertPersistedShipmentToMatchUpdatableProperties(Shipment expectedShipment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertShipmentAllUpdatablePropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
        assertShipmentUpdatableFieldsEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }
}
