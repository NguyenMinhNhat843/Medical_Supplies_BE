package com.payment.paymentservice.converter;

import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentConverter {

    @Autowired
    private ModelMapper modelMapper;

    public PaymentEntity toEntity(PaymentRequest request) {
        PaymentEntity entity = modelMapper.map(request, PaymentEntity.class);
        entity.setId(null);
        return entity;
    }

    public PaymentDTO toDTO(PaymentEntity entity) {
        return modelMapper.map(entity, PaymentDTO.class);
    }

    public List<PaymentDTO> toDTOList(List<PaymentEntity> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}