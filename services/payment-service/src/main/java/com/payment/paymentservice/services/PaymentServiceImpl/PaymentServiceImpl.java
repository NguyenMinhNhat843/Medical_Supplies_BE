package com.payment.paymentservice.services.PaymentServiceImpl;

import com.payment.paymentservice.converter.PaymentConverter;
import com.payment.paymentservice.entity.PaymentEntity;
import com.payment.paymentservice.model.dto.PaymentDTO;
import com.payment.paymentservice.model.request.PaymentRequest;
import com.payment.paymentservice.model.request.PaymentUpdateRequest;
import com.payment.paymentservice.repository.PaymentRepository;
import com.payment.paymentservice.services.PaymentService;
import com.payment.paymentservice.utils.CRC16Util;
import com.payment.paymentservice.utils.QRCodeUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentConverter paymentConverter;

    @Value("${order-service.url}") // ví dụ: http://localhost:8082
    private String orderServiceUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public PaymentDTO createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(Math.toIntExact(request.getOrderId()))) {
            throw new IllegalStateException("Đơn hàng đã có thanh toán.");
        }

        PaymentEntity payment = paymentConverter.toEntity(request);
        payment.setStatus("PENDING");

        String transferContent = null;

        if ("COD".equalsIgnoreCase(payment.getPaymentMethod())) {
            String txId = "COD-" + request.getOrderId() + "-" + System.currentTimeMillis();
            payment.setTransactionId(txId);
        } else if ("BANK_TRANSFER".equalsIgnoreCase(payment.getPaymentMethod())) {
            transferContent = "TT_ORDER_" + request.getOrderId();
            String txId = "BANK_" + request.getOrderId() + "_" + System.currentTimeMillis();
            payment.setTransactionId(txId);
        }

        payment = paymentRepository.save(payment);
        PaymentDTO dto = paymentConverter.toDTO(payment);

        // ✅ Gọi order-service cập nhật trạng thái đơn hàng
        try {
            String url = orderServiceUrl + "/api/orders/" + payment.getOrderId() + "/payment-info";
            PaymentUpdateRequest update = new PaymentUpdateRequest();
            update.setPaymentMethod(request.getPaymentMethod());
            update.setStatus("PENDING");

            if ("COD".equalsIgnoreCase(request.getPaymentMethod()) ||
                    "BANK_TRANSFER".equalsIgnoreCase(request.getPaymentMethod())) {
                update.setPaymentStatus("UNPAID");
            } else {
                update.setPaymentStatus("PAID");
            }

            restTemplate.put(url, update);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transferContent != null) {
            dto.setTransferContent(transferContent);

            // ✅ Tạo mã QR chuẩn VietQR cho VPBank
            String gui = "A000000727";
            String bankBin = "970432";
            String accountNo = "26303241603";
            String amountStr = payment.getAmount().toPlainString();

            // ✅ Build TLV fields cho Merchant Account Info (Tag 38)
            String guiField = "00" + String.format("%02d", gui.length()) + gui;
            String binField = "01" + String.format("%02d", bankBin.length()) + bankBin;
            String accField = "02" + String.format("%02d", accountNo.length()) + accountNo;

            String merchantAccountInfo = guiField + binField + accField;
            String merchantField = "38" + String.format("%02d", merchantAccountInfo.length()) + merchantAccountInfo;

            // ✅ Số tiền
            String txAmountField = "54" + String.format("%02d", amountStr.length()) + amountStr;

            // ✅ Quốc gia
            String countryCodeField = "5802VN";

            // ✅ Thông tin thêm (Tag 62 với refField là Tag 05)
            String refField = "05" + String.format("%02d", transferContent.length()) + transferContent;
            String addDataField = "62" + String.format("%02d", refField.length()) + refField;

            // ✅ Build chuỗi gốc để tính CRC
            String qrBase = "000201" +
                    "010212" +
                    merchantField +
                    "52040000" +
                    "5303704" +
                    txAmountField +
                    countryCodeField +
                    addDataField +
                    "6304";

            String crc = CRC16Util.calculateCRC(qrBase);
            String fullQR = qrBase + crc;

            dto.setQrRawContent(fullQR);
            dto.setQrCodeBase64(QRCodeUtil.generateQRCodeBase64(fullQR));
        }

        return dto;
    }

    @Override
    public PaymentDTO updatePaymentStatus(Long id, String status) {
        PaymentEntity payment = paymentRepository.findById(Math.toIntExact(id)).orElse(null);
        if (payment == null) return null;
        payment.setStatus(status);
        return paymentConverter.toDTO(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
        return null;
    }


    @Override
    public void confirmBankTransfer(Long orderId) {
        PaymentEntity payment = paymentRepository.findPaymentByOrOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thanh toán với orderId: " + orderId));
        if (payment == null) {
            throw new EntityNotFoundException("Không tìm thấy thanh toán với orderId: " + orderId);
        }

        payment.setStatus("PAID");
        paymentRepository.save(payment);

        PaymentUpdateRequest update = new PaymentUpdateRequest();
        update.setPaymentMethod("BANK_TRANSFER");
        update.setStatus("PENDING");
        update.setPaymentStatus("PAID");

        String url = orderServiceUrl + "/api/orders/" + orderId + "/payment-info";
        restTemplate.put(url, update);
    }
}
