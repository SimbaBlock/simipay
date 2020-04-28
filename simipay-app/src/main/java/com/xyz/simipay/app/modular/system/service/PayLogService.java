package com.xyz.simipay.app.modular.system.service;

import com.xyz.simipay.app.modular.system.model.PayLog;


public interface PayLogService {

    PayLog findByUidAndCode(String uid, String code);

    int inserPayLog(PayLog payLog);

}
