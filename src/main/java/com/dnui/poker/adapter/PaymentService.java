package com.dnui.poker.adapter;

public interface PaymentService {
    /**
     * 支付，返回二维码图片URL
     */
    String pay(String userId, double amount);
}

