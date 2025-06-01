package com.soft.erp.invoice.service.impl;

import com.soft.erp.invoice.repository.ShipmentRepository;
import com.soft.erp.invoice.service.ShipmentService;
import com.soft.erp.invoice.service.dto.ShipmentDTO;
import com.soft.erp.invoice.service.mapper.ShipmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.soft.erp.invoice.domain.Shipment}.
 */
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentServiceImpl.class);

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository, ShipmentMapper shipmentMapper) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
    }

    @Override
    public Mono<ShipmentDTO> save(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to save Shipment : {}", shipmentDTO);
        return shipmentRepository.save(shipmentMapper.toEntity(shipmentDTO)).map(shipmentMapper::toDto);
    }

    @Override
    public Mono<ShipmentDTO> update(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to update Shipment : {}", shipmentDTO);
        return shipmentRepository.save(shipmentMapper.toEntity(shipmentDTO)).map(shipmentMapper::toDto);
    }

    @Override
    public Mono<ShipmentDTO> partialUpdate(ShipmentDTO shipmentDTO) {
        LOG.debug("Request to partially update Shipment : {}", shipmentDTO);

        return shipmentRepository
            .findById(shipmentDTO.getId())
            .map(existingShipment -> {
                shipmentMapper.partialUpdate(existingShipment, shipmentDTO);

                return existingShipment;
            })
            .flatMap(shipmentRepository::save)
            .map(shipmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ShipmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Shipments");
        return shipmentRepository.findAllBy(pageable).map(shipmentMapper::toDto);
    }

    public Flux<ShipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return shipmentRepository.findAllWithEagerRelationships(pageable).map(shipmentMapper::toDto);
    }

    public Mono<Long> countAll() {
        return shipmentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ShipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Shipment : {}", id);
        return shipmentRepository.findOneWithEagerRelationships(id).map(shipmentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Shipment : {}", id);
        return shipmentRepository.deleteById(id);
    }
}
