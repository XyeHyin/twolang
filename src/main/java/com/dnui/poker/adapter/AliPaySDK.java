package com.dnui.poker.adapter;

// 支付宝SDK
public class AliPaySDK {
    public String aliPay(String account, double money) {
        // ...调用支付宝支付...
        // 假设返回二维码URL
        return "https://alipay.qr/" + account + "/" + (int)money;
    }
}
