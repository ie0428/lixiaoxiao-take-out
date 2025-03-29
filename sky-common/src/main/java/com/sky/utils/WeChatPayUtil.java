package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WeChatPayUtil {

    /**
     * 模拟支付（完全跳过签名和证书验证）
     */
    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        JSONObject jsonObject = new JSONObject();
        // 模拟微信支付接口返回的必要字段
        jsonObject.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        jsonObject.put("nonceStr", RandomStringUtils.randomAlphanumeric(32));
        jsonObject.put("package", "prepay_id=mock_prepay_id_" + RandomStringUtils.randomNumeric(10));
        jsonObject.put("signType", "RSA");
        jsonObject.put("paySign", "mock_sign_" + RandomStringUtils.randomAlphanumeric(64));
        jsonObject.put("code", "SUCCESS");
        return jsonObject;
    }

    /**
     * 模拟退款（直接返回成功）
     */
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) {
        JSONObject response = new JSONObject();
        response.put("code", "SUCCESS");
        response.put("message", "模拟退款成功");
        return response.toJSONString();
    }

    // 移除 getClient()、post()、get() 等与真实微信API交互的方法
}