package com.example.inventory.service;

import com.example.inventory.domain.Inventory;
import com.example.inventory.domain.OrderMessageDTO;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.service.dto.InventoryDTO;
import com.example.inventory.service.mapper.InventoryMapper;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.example.inventory.domain.Inventory}.
 */
@Service
@Transactional
public class InventoryService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    private final KafkaTemplate<String,OrderMessageDTO> kafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper,KafkaTemplate<String,OrderMessageDTO> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "INVENTORY_TOPIC",groupId = "inventory-group")
    public void doInventoryStock(String paymentInfo) throws JsonProcessingException {
        OrderMessageDTO orderMessageDTO = new ObjectMapper().readValue(paymentInfo, OrderMessageDTO.class);
        for(String productId : orderMessageDTO.getProductIds()){
            Optional<Inventory> oneByProductId = this.findOneByProductId(productId);
            oneByProductId.get().setQuantity(oneByProductId.get().getQuantity()-1);
            inventoryRepository.save(oneByProductId.get());
        }
        kafkaTemplate.send("PAYMENT_TOPIC",orderMessageDTO);
        LOG.debug("Saved Inventory Successfully {}",orderMessageDTO);

    }

    @KafkaListener(topics = "CANCEL_INVENTORY_TOPIC",groupId = "cancel-inventory-group")
    public void doInventoryStockCancel(String paymentInfo) throws JsonProcessingException {
        OrderMessageDTO orderMessageDTO = new ObjectMapper().readValue(paymentInfo, OrderMessageDTO.class);
        for(String productId : orderMessageDTO.getProductIds()){
            Optional<Inventory> oneByProductId = this.findOneByProductId(productId);
            oneByProductId.get().setQuantity(oneByProductId.get().getQuantity()+1);
            inventoryRepository.save(oneByProductId.get());
            LOG.debug("Canceled Inventory Successfully");
        }
        kafkaTemplate.send("CANCEL_ORDER_TOPIC",orderMessageDTO);
    }

    /**
     * Save a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryDTO save(InventoryDTO inventoryDTO) {
        LOG.debug("Request to save Inventory : {}", inventoryDTO);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(inventory);
    }

    /**
     * Update a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryDTO update(InventoryDTO inventoryDTO) {
        LOG.debug("Request to update Inventory : {}", inventoryDTO);
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDto(inventory);
    }

    /**
     * Partially update a inventory.
     *
     * @param inventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO) {
        LOG.debug("Request to partially update Inventory : {}", inventoryDTO);

        return inventoryRepository
            .findById(inventoryDTO.getId())
            .map(existingInventory -> {
                inventoryMapper.partialUpdate(existingInventory, inventoryDTO);

                return existingInventory;
            })
            .map(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }

    /**
     * Get all the inventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Inventories");
        return inventoryRepository.findAll(pageable).map(inventoryMapper::toDto);
    }

    /**
     * Get one inventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> findOne(Long id) {
        LOG.debug("Request to get Inventory : {}", id);
        return inventoryRepository.findById(id).map(inventoryMapper::toDto);
    }

    /**
     * Delete the inventory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Inventory : {}", id);
        inventoryRepository.deleteById(id);
    }

    public Optional<Inventory> findOneByProductId(String id) {
        return inventoryRepository.findByProductId(id);
    }
}
