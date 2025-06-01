package com.soft.erp.product.web.rest;

import static com.soft.erp.product.domain.ProductOrderAsserts.*;
import static com.soft.erp.product.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soft.erp.product.IntegrationTest;
import com.soft.erp.product.domain.ProductOrder;
import com.soft.erp.product.domain.enumeration.OrderStatus;
import com.soft.erp.product.repository.EntityManager;
import com.soft.erp.product.repository.ProductOrderRepository;
import com.soft.erp.product.service.dto.ProductOrderDTO;
import com.soft.erp.product.service.mapper.ProductOrderMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProductOrderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductOrderResourceIT {

    private static final Instant DEFAULT_PLACED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLACED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.COMPLETED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.PENDING;

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_INVOICE_ID = 1L;
    private static final Long UPDATED_INVOICE_ID = 2L;

    private static final String DEFAULT_CUSTOMER = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/product-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductOrder productOrder;

    private ProductOrder insertedProductOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductOrder createEntity() {
        return new ProductOrder()
            .placedDate(DEFAULT_PLACED_DATE)
            .status(DEFAULT_STATUS)
            .code(DEFAULT_CODE)
            .invoiceId(DEFAULT_INVOICE_ID)
            .customer(DEFAULT_CUSTOMER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductOrder createUpdatedEntity() {
        return new ProductOrder()
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .invoiceId(UPDATED_INVOICE_ID)
            .customer(UPDATED_CUSTOMER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductOrder.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        productOrder = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProductOrder != null) {
            productOrderRepository.delete(insertedProductOrder).block();
            insertedProductOrder = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProductOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);
        var returnedProductOrderDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProductOrderDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ProductOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProductOrder = productOrderMapper.toEntity(returnedProductOrderDTO);
        assertProductOrderUpdatableFieldsEquals(returnedProductOrder, getPersistedProductOrder(returnedProductOrder));

        insertedProductOrder = returnedProductOrder;
    }

    @Test
    void createProductOrderWithExistingId() throws Exception {
        // Create the ProductOrder with an existing ID
        productOrder.setId(1L);
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkPlacedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productOrder.setPlacedDate(null);

        // Create the ProductOrder, which fails.
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productOrder.setStatus(null);

        // Create the ProductOrder, which fails.
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productOrder.setCode(null);

        // Create the ProductOrder, which fails.
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCustomerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        productOrder.setCustomer(null);

        // Create the ProductOrder, which fails.
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductOrders() {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        // Get all the productOrderList
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
            .value(hasItem(productOrder.getId().intValue()))
            .jsonPath("$.[*].placedDate")
            .value(hasItem(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].invoiceId")
            .value(hasItem(DEFAULT_INVOICE_ID.intValue()))
            .jsonPath("$.[*].customer")
            .value(hasItem(DEFAULT_CUSTOMER));
    }

    @Test
    void getProductOrder() {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        // Get the productOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productOrder.getId().intValue()))
            .jsonPath("$.placedDate")
            .value(is(DEFAULT_PLACED_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.invoiceId")
            .value(is(DEFAULT_INVOICE_ID.intValue()))
            .jsonPath("$.customer")
            .value(is(DEFAULT_CUSTOMER));
    }

    @Test
    void getNonExistingProductOrder() {
        // Get the productOrder
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProductOrder() throws Exception {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productOrder
        ProductOrder updatedProductOrder = productOrderRepository.findById(productOrder.getId()).block();
        updatedProductOrder
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .invoiceId(UPDATED_INVOICE_ID)
            .customer(UPDATED_CUSTOMER);
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(updatedProductOrder);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProductOrderToMatchAllProperties(updatedProductOrder);
    }

    @Test
    void putNonExistingProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productOrderDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductOrderWithPatch() throws Exception {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productOrder using partial update
        ProductOrder partialUpdatedProductOrder = new ProductOrder();
        partialUpdatedProductOrder.setId(productOrder.getId());

        partialUpdatedProductOrder
            .placedDate(UPDATED_PLACED_DATE)
            .code(UPDATED_CODE)
            .invoiceId(UPDATED_INVOICE_ID)
            .customer(UPDATED_CUSTOMER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProductOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProductOrder, productOrder),
            getPersistedProductOrder(productOrder)
        );
    }

    @Test
    void fullUpdateProductOrderWithPatch() throws Exception {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the productOrder using partial update
        ProductOrder partialUpdatedProductOrder = new ProductOrder();
        partialUpdatedProductOrder.setId(productOrder.getId());

        partialUpdatedProductOrder
            .placedDate(UPDATED_PLACED_DATE)
            .status(UPDATED_STATUS)
            .code(UPDATED_CODE)
            .invoiceId(UPDATED_INVOICE_ID)
            .customer(UPDATED_CUSTOMER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductOrder.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProductOrder))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProductOrderUpdatableFieldsEquals(partialUpdatedProductOrder, getPersistedProductOrder(partialUpdatedProductOrder));
    }

    @Test
    void patchNonExistingProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productOrderDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProductOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        productOrder.setId(longCount.incrementAndGet());

        // Create the ProductOrder
        ProductOrderDTO productOrderDTO = productOrderMapper.toDto(productOrder);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(productOrderDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProductOrder() {
        // Initialize the database
        insertedProductOrder = productOrderRepository.save(productOrder).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the productOrder
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productOrder.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return productOrderRepository.count().block();
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

    protected ProductOrder getPersistedProductOrder(ProductOrder productOrder) {
        return productOrderRepository.findById(productOrder.getId()).block();
    }

    protected void assertPersistedProductOrderToMatchAllProperties(ProductOrder expectedProductOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductOrderAllPropertiesEquals(expectedProductOrder, getPersistedProductOrder(expectedProductOrder));
        assertProductOrderUpdatableFieldsEquals(expectedProductOrder, getPersistedProductOrder(expectedProductOrder));
    }

    protected void assertPersistedProductOrderToMatchUpdatableProperties(ProductOrder expectedProductOrder) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProductOrderAllUpdatablePropertiesEquals(expectedProductOrder, getPersistedProductOrder(expectedProductOrder));
        assertProductOrderUpdatableFieldsEquals(expectedProductOrder, getPersistedProductOrder(expectedProductOrder));
    }
}
