package com.xyz.simipay.app.modular.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.xyz.simipay.app.modular.system.model.Log;

public interface LogService extends IService<Log> {

    int insertLog(Log log);

}
