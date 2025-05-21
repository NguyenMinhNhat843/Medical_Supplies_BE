package com.order.orderservice.service.impl;

import com.order.orderservice.entity.Voucher;
import com.order.orderservice.repository.VoucherRepository;
import com.order.orderservice.service.inter.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Voucher createVoucher(Voucher voucher) {
        if (voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ngày hết hạn phải sau thời gian hiện tại");
        }
        if (voucher.getMaxUsage() < 0 || voucher.getUsedCount() < 0) {
            throw new RuntimeException("Số lần sử dụng không hợp lệ");
        }
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher updateVoucher(Integer id, Voucher voucherDetails) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

        voucher.setCode(voucherDetails.getCode());
        voucher.setDiscountType(voucherDetails.getDiscountType());
        voucher.setDiscountValue(voucherDetails.getDiscountValue());
        voucher.setExpiryDate(voucherDetails.getExpiryDate());
        voucher.setMaxUsage(voucherDetails.getMaxUsage());
        voucher.setUsedCount(voucherDetails.getUsedCount());
        voucher.setActive(voucherDetails.isActive());

        if (voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Ngày hết hạn phải sau thời gian hiện tại");
        }
        if (voucher.getMaxUsage() < voucher.getUsedCount()) {
            throw new RuntimeException("Số lần tối đa phải lớn hơn hoặc bằng số lần đã sử dụng");
        }

        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
        voucherRepository.delete(voucher);
    }

    @Override
    public Optional<Voucher> getVoucherById(Integer id) {
        return voucherRepository.findById(id);
    }

    @Override
    public Optional<Voucher> getVoucherByCode(String code) {
        return voucherRepository.findByCodeAndExpiryDateAfterAndIsActiveTrue(code, LocalDateTime.now());
    }
}