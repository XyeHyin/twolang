package com.dnui.poker.adapter;

import com.dnui.poker.thirdparty.AliPaySDK;

public class AliPayAdapter implements PaymentService {
    private final AliPaySDK aliPaySDK = new AliPaySDK();

    @Override
    public String pay(String userId, double amount) {
        return aliPaySDK.aliPay(userId, amount);
    }
}