package com.order.orderservice.controller;

import com.order.orderservice.dto.ApiResponseDTO;
import com.order.orderservice.entity.Voucher;
import com.order.orderservice.service.inter.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    // Tạo mới voucher
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Voucher>> createVoucher(@RequestBody Voucher voucher) {
        try {
            Voucher createdVoucher = voucherService.createVoucher(voucher);
            return ResponseEntity.ok(
                    ApiResponseDTO.<Voucher>builder()
                            .message("Tạo voucher thành công")
                            .data(createdVoucher)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<Voucher>builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    // Cập nhật voucher
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Voucher>> updateVoucher(
            @PathVariable Integer id, @RequestBody Voucher voucherDetails) {
        try {
            Voucher updatedVoucher = voucherService.updateVoucher(id, voucherDetails);
            return ResponseEntity.ok(
                    ApiResponseDTO.<Voucher>builder()
                            .message("Cập nhật voucher thành công")
                            .data(updatedVoucher)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<Voucher>builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    // Xóa voucher
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteVoucher(@PathVariable Integer id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.<Void>builder()
                            .message("Xóa voucher thành công")
                            .data(null)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<Void>builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

    // Lấy thông tin voucher
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Voucher>> getVoucherById(@PathVariable Integer id) {
        Optional<Voucher> voucher = voucherService.getVoucherById(id);
        return voucher.map(v -> ResponseEntity.ok(
                ApiResponseDTO.<Voucher>builder()
                        .message("Lấy thông tin voucher thành công")
                        .data(v)
                        .build()
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDTO.<Voucher>builder()
                        .message("Voucher không tồn tại")
                        .data(null)
                        .build()
        ));
    }
}