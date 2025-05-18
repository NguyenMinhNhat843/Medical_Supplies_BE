package com.auth.authservice.service.serviceImpl;

import com.auth.authservice.model.request.UserRegisterRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class OtpService {
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final Map<String, UserRegisterRequest> pendingRegistration = new ConcurrentHashMap<>();

    public String generateOtp(String email, Long userId) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, new OtpEntry(otp, System.currentTimeMillis() + 5 * 60 * 1000, userId));
        return otp;
    }

    public boolean validateOtp(String email, String inputOtp) {
        OtpEntry entry = otpStore.get(email);
        return entry != null && System.currentTimeMillis() <= entry.expiry && entry.otp.equals(inputOtp);
    }

    public void clearOtp(String email) {
        otpStore.remove(email);
        pendingRegistration.remove(email);
    }

    // Lưu trữ thông tin đăng ký tạm thời
    public void storeRegistrationInfo(String email, UserRegisterRequest request) {
        pendingRegistration.put(email, request);
    }

    // Lấy thông tin đăng ký tạm thời
    public UserRegisterRequest getRegistrationInfo(String email) {
        return pendingRegistration.get(email);
    }

    public Long getUserIdFromOtpStore(String email) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) {
            log.warn("Không tìm thấy OTP entry cho email: {}", email);
            return null;
        }
        log.info("✅ Found OTP entry with userId: {} for email: {}", entry.userId, email);
        return entry.userId;
    }

    static class OtpEntry {
        String otp;
        long expiry;
        Long userId;
        public OtpEntry(String otp, long expiry, Long userId) {
            this.otp = otp;
            this.expiry = expiry;
            this.userId = userId;
        }
    }


}

