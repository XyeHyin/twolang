package com.dnui.poker.adapter;

public interface PaymentService {
    /**
     * 支付，返回二维码图片URL
     */
    String pay(String userId, double amount);
}

// 支付宝SDK
public class AliPaySDK {
    public boolean aliPay(String account, double money) {
        // ...调用支付宝支付...
        return true;
    }
}

// 微信SDK
public class WechatPaySDK {
    public boolean wxPay(String openId, double money) {
        // ...调用微信支付...
        return true;
    }
}