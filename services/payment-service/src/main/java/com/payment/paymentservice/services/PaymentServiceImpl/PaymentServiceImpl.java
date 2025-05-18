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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    // Tạo QR code cho chuyển khoản ngân hàng
//    @Override
//    public PaymentDTO createPayment(PaymentRequest request) {
//        if (paymentRepository.existsByOrderId(Math.toIntExact(request.getOrderId()))) {
//            throw new IllegalStateException("Đơn hàng đã có thanh toán.");
//        }
//
//        PaymentEntity payment = paymentConverter.toEntity(request);
//        payment.setStatus("PENDING");
//
//        String transferContent = null;
//
//        if ("COD".equalsIgnoreCase(payment.getPaymentMethod())) {
//            String txId = "COD-" + request.getOrderId() + "-" + System.currentTimeMillis();
//            payment.setTransactionId(txId);
//        } else if ("BANK_TRANSFER".equalsIgnoreCase(payment.getPaymentMethod())) {
//            transferContent = "TT_ORDER_" + request.getOrderId();
//            String txId = "BANK_" + request.getOrderId() + "_" + System.currentTimeMillis();
//            payment.setTransactionId(txId);
//        }
//
//        payment = paymentRepository.save(payment);
//        PaymentDTO dto = paymentConverter.toDTO(payment);
//
//        try {
//            String url = orderServiceUrl + "/api/orders/" + payment.getOrderId() + "/payment-info";
//            PaymentUpdateRequest update = new PaymentUpdateRequest();
//            update.setPaymentMethod(request.getPaymentMethod());
//            update.setStatus("PENDING");
//
//            if ("COD".equalsIgnoreCase(request.getPaymentMethod()) ||
//                    "BANK_TRANSFER".equalsIgnoreCase(request.getPaymentMethod())) {
//                update.setPaymentStatus("UNPAID");
//            } else {
//                update.setPaymentStatus("PAID");
//            }
//
//            restTemplate.put(url, update);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (transferContent != null) {
//            dto.setTransferContent(transferContent);
//
//            String gui = "A000000727";
//            String bankBin = "970432";
//            final String accountNo = "26303241603";
//            BigDecimal amount = payment.getAmount().setScale(0, RoundingMode.DOWN);
//            String amountStr = amount.toPlainString(); // sẽ ra "150000"
//            String guiField = "00" + String.format("%02d", gui.getBytes(StandardCharsets.UTF_8).length) + gui;
//            String binField = "01" + String.format("%02d", bankBin.getBytes(StandardCharsets.UTF_8).length) + bankBin;
//            byte[] accBytes = accountNo.getBytes(StandardCharsets.UTF_8);
//            String accField = "02" + String.format("%02d", accBytes.length) + accountNo;
//            String merchantAccountInfo = guiField + binField + accField;
//            String merchantField = "26" + String.format("%02d", merchantAccountInfo.getBytes(StandardCharsets.UTF_8).length) + merchantAccountInfo;
//            System.out.println("✅ accountNo bytes = " + Arrays.toString(accBytes));
//            System.out.println("accField = " + accField);
//            for (int i = 0; i < accountNo.length(); i++) {
//                System.out.printf("Char %d: %c (byte=%d)\n", i, accountNo.charAt(i), (int) accountNo.charAt(i));
//            }
//            String txAmountField = "54" + String.format("%02d", amountStr.getBytes(StandardCharsets.UTF_8).length) + amountStr;
//            String countryCodeField = "5802VN";
//
//            String merchantName = "LE PHUOC NGUYEN";
//            String merchantCity = "HO CHI MINH";
//            String merchantNameField = "59" + String.format("%02d", merchantName.getBytes(StandardCharsets.UTF_8).length) + merchantName;
//            String merchantCityField = "60" + String.format("%02d", merchantCity.getBytes(StandardCharsets.UTF_8).length) + merchantCity;
//
//            String refField = "08" + String.format("%02d", transferContent.getBytes(StandardCharsets.UTF_8).length) + transferContent;
//            String addDataField = "62" + String.format("%02d", refField.getBytes(StandardCharsets.UTF_8).length) + refField;
//
//            String qrBase = "000201" +
//                    "010212" +
//                    merchantField +
//                    "52040000" +
//                    "5303704" +
//                    txAmountField +
//                    countryCodeField +
//                    merchantNameField +
//                    merchantCityField +
//                    addDataField +
//                    "6304";
//
//            String crc = CRC16Util.calculateCRC(qrBase);
//            String fullQR = qrBase + crc;
//            System.out.println("guiField = " + guiField);
//            System.out.println("binField = " + binField);
//            System.out.println("accField = " + accField);
//            System.out.println("merchantAccountInfo = " + merchantAccountInfo);
//            System.out.println("merchantField = " + merchantField);
//            logQRBreakdown(fullQR);
//            System.out.println("fullQR = " + fullQR);
//
//            if (!fullQR.matches("^[\\x20-\\x7E]+$")) {
//                throw new IllegalStateException("QR raw content contains invalid characters.");
//            }
//            System.out.println("✅ CRC = " + crc);
//            dto.setQrRawContent(fullQR);
//            dto.setQrCodeBase64(QRCodeUtil.generateQRCodeBase64(fullQR));
//        }
//
//        return dto;
//    }


    // Tạo QR bằng VietQR
    @Override
    public PaymentDTO createPayment(PaymentRequest request) {
        if (paymentRepository.existsByOrderId(Math.toIntExact(request.getOrderId()))) {
            throw new IllegalStateException("Đơn hàng đã được đặt.");
        }

        PaymentEntity payment = paymentConverter.toEntity(request);
        payment.setStatus("PENDING");

        String transferContent = null;

        // Tạo transactionId
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

        // Gọi order-service để cập nhật trạng thái đơn hàng
        try {
            String url = orderServiceUrl + "/api/orders/" + payment.getOrderId() + "/payment-info";
            PaymentUpdateRequest update = new PaymentUpdateRequest();
            update.setPaymentMethod(request.getPaymentMethod());
            update.setStatus("PENDING");

            if ("BANK_TRANSFER".equalsIgnoreCase(request.getPaymentMethod())) {
                update.setPaymentStatus("PAID");
            } else if ("COD".equalsIgnoreCase(request.getPaymentMethod())) {
                update.setPaymentStatus("UNPAID");
            } else {
                update.setPaymentStatus("PAID");
            }

            restTemplate.put(url, update);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  Nếu là chuyển khoản thì gán transferContent + ảnh QR từ VietQR
        if (transferContent != null) {
            dto.setTransferContent(transferContent);

            // Dữ liệu tài khoản cố định
            String bankBin = "970432";
            String accountNo = "26303241603";
            String accountName = URLEncoder.encode("LE PHUOC NGUYEN", StandardCharsets.UTF_8);
            long amount = payment.getAmount().longValue();

            // Template ID lấy từ VietQR dashboard
            String templateId = "75qHkWq";

            String qrImageUrl = String.format(
                    "https://api.vietqr.io/image/%s-%s-%s.jpg?accountName=%s&amount=%d",
                    bankBin, accountNo, templateId, accountName, amount
            );

            dto.setQrCodeImageUrl(qrImageUrl); // frontend dùng ảnh này để render
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

    // Xác nhận chuyển khoản ngân hàng thủ công
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


    // Debug QR content chuẩn CRC16
    public void logQRBreakdown(String qrRawContent) {
        System.out.println("\n🔍 Breakdown QR Content:");
        int i = 0;
        while (i + 4 <= qrRawContent.length()) {
            String tag = qrRawContent.substring(i, i + 2);
            String lengthStr = qrRawContent.substring(i + 2, i + 4);
            int len;
            try {
                len = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                System.out.println("Lỗi đọc length tại tag: " + tag);
                break;
            }
            int valueEnd = i + 4 + len;
            if (valueEnd > qrRawContent.length()) {
                System.out.printf("Tag %s: Độ dài %d vượt quá chuỗi.\n", tag, len);
                break;
            }
            String value = qrRawContent.substring(i + 4, valueEnd);
            System.out.printf("Tag %s | Len: %s | Value: %s\n", tag, lengthStr, value);
            i = valueEnd;
        }
        System.out.println("➡Tổng độ dài: " + qrRawContent.length());
    }
}
