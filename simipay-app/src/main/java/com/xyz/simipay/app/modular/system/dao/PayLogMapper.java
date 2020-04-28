package com.xyz.simipay.app.modular.system.dao;

import com.xyz.simipay.app.modular.system.model.PayLog;
import org.apache.ibatis.annotations.Param;


public interface PayLogMapper {

    PayLog findByUidAndCode(@Param("uid") String uid, @Param("code") String code);

    int inserPayLog(PayLog payLog);

}
