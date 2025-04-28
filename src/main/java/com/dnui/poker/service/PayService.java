package com.dnui.poker.service;

import com.dnui.poker.adapter.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PayService {
    private PaymentService paymentService;

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String pay(String userId, double amount) {
        return paymentService.pay(userId, amount);
    }
}