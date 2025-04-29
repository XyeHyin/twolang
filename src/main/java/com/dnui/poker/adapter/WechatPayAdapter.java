package com.dnui.poker.adapter;

import com.dnui.poker.thirdparty.WechatPaySDK;

public class WechatPayAdapter implements PaymentService {
    private final com.dnui.poker.adapter.WechatPaySDK wechatPaySDK = new com.dnui.poker.adapter.WechatPaySDK();

    @Override
    public String pay(String userId, double amount) {
        // 适配SDK返回二维码URL
        return wechatPaySDK.wxPay(userId, amount);
    }
}