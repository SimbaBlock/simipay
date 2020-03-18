package com.xyz.simipay.app.modular.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.simipay.app.modular.system.model.Log;

public interface LogMapper extends BaseMapper<Log> {

    int insertLog(Log log);

}
