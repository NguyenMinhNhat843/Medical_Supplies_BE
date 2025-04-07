package com.user.userservice.service.serviceImpl;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

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
    }

    public Long getUserIdFromOtpStore(String email) {
        OtpEntry entry = otpStore.get(email);
        return entry != null ? entry.userId : null;
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

