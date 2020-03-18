package com.xyz.simipay.test;

import com.alibaba.fastjson.JSONObject;
import com.xyz.simipay.app.core.util.EncryptUtil;
import com.xyz.simipay.app.core.util.HttpUtil;
import org.junit.Test;

import java.util.Date;
import java.util.TreeMap;

public class ApiTest extends ApplicationTest {

    @Test
    public void getBalanceTest() {

        TreeMap<String, String> queryParas = new TreeMap<>();
        queryParas.put("access_key", "你的access_key");
        queryParas.put("uid", "你的uid");
        queryParas.put("tnonce", new Date().getTime()+"");

        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/getBalance", "你的key");
        queryParas.put("signature",sign);

        String reulst = HttpUtil.doPost("http://localhost:8422/api/getBalance",queryParas);

        JSONObject json = (JSONObject) JSONObject.parse(reulst);
        System.out.println(json);

    }

    @Test
    public void updateBalanceTest() {

        TreeMap<String, String> queryParas = new TreeMap<>();
        queryParas.put("access_key", "你的access_key");
        queryParas.put("uid", "你的uid");
        queryParas.put("change", "1");
        queryParas.put("tnonce", new Date().getTime()+"");

        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/updateBalance", "你的key");
        queryParas.put("signature",sign);

        String reulst = HttpUtil.doPost("http://localhost:8422/api/updateBalance",queryParas);

        JSONObject json = (JSONObject) JSONObject.parse(reulst);
        System.out.println(json);

    }

    @Test
    public void addUserTest() {

        TreeMap<String, String> queryParas = new TreeMap<>();
        queryParas.put("access_key", "你的access_key");
        queryParas.put("uid", "你的uid");
        queryParas.put("tnonce", new Date().getTime()+"");

        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/addUser", "你的key");
        queryParas.put("signature",sign);

        String reulst = HttpUtil.doPost("http://localhost:8422/api/addUser",queryParas);

        JSONObject json = (JSONObject) JSONObject.parse(reulst);
        System.out.println(json);

    }

    @Test
    public void getUserAllBalanceTest() {

        TreeMap<String, String> queryParas = new TreeMap<>();
        queryParas.put("access_key", "你的access_key");
        queryParas.put("tnonce", new Date().getTime()+"");

        String sign = EncryptUtil.sha256_HMAC(queryParas, "/api/getUserAllBalance", "你的key");
        queryParas.put("signature",sign);
        queryParas.put("page","1");

        String reulst = HttpUtil.doPost("http://localhost:8422/api/getUserAllBalance",queryParas);

        JSONObject json = (JSONObject) JSONObject.parse(reulst);
        System.out.println(json);

    }

}
