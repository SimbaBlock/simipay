package com.xyz.simipay.app.modular.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xyz.simipay.app.modular.system.model.Access;
import org.apache.ibatis.annotations.Param;

public interface AccessMapper extends BaseMapper<Access> {

    Access findByAccessKey(@Param("accesskey") String accesskey);

}
