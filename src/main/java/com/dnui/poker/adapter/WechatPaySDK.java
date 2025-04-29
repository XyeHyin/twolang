package com.dnui.poker.adapter;

// 微信SDK
public class WechatPaySDK {
    public String wxPay(String openId, double money) {
        // ...调用微信支付...
        // 假设返回二维码URL
        return "https://wxpay.qr/" + openId + "/" + (int)money;
    }
}
