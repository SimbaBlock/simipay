package com.xyz.simipay.app.modular.system.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz.simipay.app.modular.system.dao.LogMapper;
import com.xyz.simipay.app.modular.system.model.Log;
import com.xyz.simipay.app.modular.system.service.LogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Resource
    private LogMapper logMapper;

    @Override
    public int insertLog(Log log) {
        return logMapper.insertLog(log);
    }

}
