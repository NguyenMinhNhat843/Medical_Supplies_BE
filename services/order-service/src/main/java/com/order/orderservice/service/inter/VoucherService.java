package com.order.orderservice.service.inter;

import com.order.orderservice.entity.Voucher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface VoucherService {
    Voucher createVoucher(Voucher voucher);
    Voucher updateVoucher(Integer id, Voucher voucherDetails);
    void deleteVoucher(Integer id);
    Optional<Voucher> getVoucherById(Integer id);
    Optional<Voucher> getVoucherByCode(String code);
}