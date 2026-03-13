package com.hallsymphony.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateUserId() {
        return "U-" + UUID.randomUUID();
    }

    public static String generateBookingId() {
        return "B-" + UUID.randomUUID();
    }

    public static String generatePaymentId() {
        return "P-" + UUID.randomUUID();
    }
}
