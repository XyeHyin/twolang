package com.dnui.poker.service;

import com.dnui.poker.adapter.PaymentService;
import com.dnui.poker.adapter.WechatPayAdapter;
import com.dnui.poker.adapter.AliPayAdapter;
import org.springframework.stereotype.Service;

// 适配器模式：通过PaymentService适配不同支付实现
@Service
public class PayService {
    private PaymentService paymentService;

    public PayService() {
        // 默认使用微信支付适配器
        this.paymentService = new WechatPayAdapter();
        // 如需切换支付宝支付：this.paymentService = new AliPayAdapter();
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String pay(String userId, double amount) {
        return paymentService.pay(userId, amount);
    }
}