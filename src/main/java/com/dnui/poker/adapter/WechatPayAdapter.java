package com.dnui.poker.adapter;

import com.dnui.poker.thirdparty.WechatPaySDK;

public class WechatPayAdapter implements PaymentService {
    private final WechatPaySDK wechatPaySDK = new WechatPaySDK();

    @Override
    public String pay(String userId, double amount) {
        return wechatPaySDK.wxPay(userId, amount);
    }
}