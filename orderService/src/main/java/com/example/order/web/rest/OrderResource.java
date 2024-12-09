package com.example.order.web.rest;

import com.example.order.domain.InventoryDTO;
import com.example.order.domain.OrderFullDto;
import com.example.order.domain.OrderMessageDTO;
import com.example.order.domain.ProductDTO;
import com.example.order.repository.OrderRepository;
import com.example.order.service.OrderService;
import com.example.order.service.dto.OrderDTO;
import com.example.order.web.rest.errors.BadRequestAlertException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.order.domain.Order}.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderResource.class);

    private KafkaTemplate<String, OrderMessageDTO> paymentKafkaTemplate;
    private final String topic ="INVENTORY_TOPIC";

    private static final String ENTITY_NAME = "orderServiceOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderService orderService;

    private final OrderRepository orderRepository;

    public OrderResource(OrderService orderService, OrderRepository orderRepository,KafkaTemplate<String,OrderMessageDTO> paymentKafkaTemplate ) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.paymentKafkaTemplate = paymentKafkaTemplate;
    }

    @KafkaListener(topics = "CANCEL_ORDER_TOPIC",groupId = "cancel-order-group")
    public void doInventoryStockCancel(String paymentInfo) throws JsonProcessingException {
        OrderMessageDTO orderMessageDTO = new ObjectMapper().readValue(paymentInfo, OrderMessageDTO.class);
        Optional<OrderDTO> one = this.orderService.findOne(orderMessageDTO.getOrderId());
        one.get().setStatus("CANCELED");
        orderService.save(one.get());
        LOG.debug("CANCELED ORDER {}",one.get().getId());
    }

    /**
     * {@code POST  /orders} : Create a new order.
     *
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderDTO, or with status {@code 400 (Bad Request)} if the order has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderFullDto order) throws URISyntaxException {
        LOG.debug("REST request to save Order : {}", order);

        RestTemplate restTemplate = new RestTemplate();

        BigDecimal total = new BigDecimal(0);

        for (String productId : order.getIds()) {
            // Check product quantity
            String quantityUrl = "http://localhost:8080/api/inventories/product/" + productId;
            ResponseEntity<InventoryDTO> quantityResponse = restTemplate.getForEntity(quantityUrl, InventoryDTO.class);

            if (!quantityResponse.getStatusCode().is2xxSuccessful() || quantityResponse.getBody() == null) {
                throw new BadRequestAlertException("Unable to fetch quantity for product ID: " + productId, "Order", "quantityError");
            }

            Integer quantity = quantityResponse.getBody().getQuantity();
            if (quantity <= 0) {
                throw new BadRequestAlertException("Product ID " + productId + " is out of stock", "Order", "outOfStock");
            }

            // Check product price
            String priceUrl = "http://localhost:8080/api/products/" + productId;
            ResponseEntity<ProductDTO> priceResponse = restTemplate.getForEntity(priceUrl, ProductDTO.class);

            if (!priceResponse.getStatusCode().is2xxSuccessful() || priceResponse.getBody() == null) {
                throw new BadRequestAlertException("Unable to fetch price for product ID: " + productId, "Order", "priceError");
            }

            BigDecimal price = priceResponse.getBody().getPrice();
            total = new BigDecimal(total.intValue() + price.intValue());
            LOG.debug("Product ID: {} has quantity: {} and price: {}", productId, 0, price);

            // You can perform additional logic with the fetched price if needed
        }

        // Assuming all validations pass, save the order
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCreatedAt(Instant.now());
        orderDTO.setCustomerId(order.getCustomerId());
        orderDTO.setUpdatedAt(Instant.now());
        orderDTO.setStatus("PASSED");
        orderDTO.setTotalAmount(total);
        orderDTO = orderService.save(orderDTO);

        OrderMessageDTO p = new OrderMessageDTO();
        p.setCustomerId(orderDTO.getCustomerId());
        p.setOrderId(orderDTO.getId());
        p.setTotalAmount(orderDTO.getTotalAmount());

        p.setProductIds(order.getIds());
        paymentKafkaTemplate.send(topic,p);

        return ResponseEntity.created(new URI("/api/orders/" + orderDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, orderDTO.getId().toString()))
            .body(orderDTO);
    }

    /**
     * {@code PUT  /orders/:id} : Updates an existing order.
     *
     * @param id the id of the orderDTO to save.
     * @param orderDTO the orderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDTO,
     * or with status {@code 400 (Bad Request)} if the orderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//    @PutMapping("/{id}")
//    public ResponseEntity<OrderDTO> updateOrder(
//        @PathVariable(value = "id", required = false) final Long id,
//        @Valid @RequestBody OrderDTO orderDTO
//    ) throws URISyntaxException {
//        LOG.debug("REST request to update Order : {}, {}", id, orderDTO);
//        if (orderDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, orderDTO.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!orderRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        orderDTO = orderService.update(orderDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderDTO.getId().toString()))
//            .body(orderDTO);
//    }

    /**
     * {@code PATCH  /orders/:id} : Partial updates given fields of an existing order, field will ignore if it is null
     *
     * @param id the id of the orderDTO to save.
     * @param orderDTO the orderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderDTO,
     * or with status {@code 400 (Bad Request)} if the orderDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
//    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
//    public ResponseEntity<OrderDTO> partialUpdateOrder(
//        @PathVariable(value = "id", required = false) final Long id,
//        @NotNull @RequestBody OrderDTO orderDTO
//    ) throws URISyntaxException {
//        LOG.debug("REST request to partial update Order partially : {}, {}", id, orderDTO);
//        if (orderDTO.getId() == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
//        }
//        if (!Objects.equals(id, orderDTO.getId())) {
//            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
//        }
//
//        if (!orderRepository.existsById(id)) {
//            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
//        }
//
//        Optional<OrderDTO> result = orderService.partialUpdate(orderDTO);
//
//        return ResponseUtil.wrapOrNotFound(
//            result,
//            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderDTO.getId().toString())
//        );
//    }

    /**
     * {@code GET  /orders} : get all the orders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OrderDTO>> getAllOrders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Orders");
        Page<OrderDTO> page = orderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /orders/:id} : get the "id" order.
     *
     * @param id the id of the orderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Order : {}", id);
        Optional<OrderDTO> orderDTO = orderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderDTO);
    }

    /**
     * {@code DELETE  /orders/:id} : delete the "id" order.
     *
     * @param id the id of the orderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Order : {}", id);
        orderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
