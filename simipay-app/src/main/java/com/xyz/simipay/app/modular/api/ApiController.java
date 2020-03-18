/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyz.simipay.app.modular.api;

import com.alibaba.fastjson.JSONObject;
import cn.stylefeng.roses.core.base.controller.BaseController;
import com.google.common.collect.Maps;
import com.xyz.simipay.app.core.common.exception.BizExceptionEnum;
import com.xyz.simipay.app.core.util.CoinApi;
import com.xyz.simipay.app.core.util.EncryptUtil;
import com.xyz.simipay.app.core.util.JsonResult;
import com.xyz.simipay.app.modular.api.vo.UserAssetsVo;
import com.xyz.simipay.app.modular.system.model.Access;
import com.xyz.simipay.app.modular.system.model.Data;
import com.xyz.simipay.app.modular.system.model.Log;
import com.xyz.simipay.app.modular.system.model.User;
import com.xyz.simipay.app.modular.system.service.AccessService;
import com.xyz.simipay.app.modular.system.service.DataService;
import com.xyz.simipay.app.modular.system.service.LogService;
import com.xyz.simipay.app.modular.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.util.*;

/**
 * 接口控制器提供
 *
 * @author stylefeng
 * @Date 2018/7/20 23:39
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private AccessService accessService;

    @Autowired
    private LogService logService;

    static final String openid = "1001";

    /**
     * 获取用户资产
     * @param access_key
     * @param uid
     * @param tnonce
     * @param signature
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value="/getBalance", method = RequestMethod.POST)
    public JsonResult findUtxoByAddress(String access_key, String uid, String tnonce, String signature) throws Exception {

        if (access_key == null || uid == null || tnonce == null || signature == null)
            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());

        Long tonceLong = Long.valueOf(tnonce) + 15000;
        if (new Date().getTime() > tonceLong)
            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());

        Access access = accessService.findByAccessKey(access_key);
        TreeMap<String, String> queryParas  = new TreeMap<>();
        queryParas.put("access_key", access_key);
        queryParas.put("uid", uid);
        queryParas.put("tnonce", tnonce);
        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/getBalance", access.getKey());


        if (!signature.equals(sign))
            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());


        User user = userService.findByUserId(Integer.valueOf(uid));

        JSONObject json = CoinApi.queryBalance(user.getSystemId());

        JSONObject result = json.getJSONObject("result");

        JSONObject coin = result.getJSONObject("SIMI");

        String available = coin.getString("available");

        return new JsonResult().addData("available", available);

    }

    /**
     * 更新用户资产
     * @param access_key
     * @param uid
     * @param tnonce
     * @param signature
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value="/updateBalance", method = RequestMethod.POST)
    public JsonResult updateBalance(String access_key, String uid, String change, String tnonce, String signature) throws Exception {

        if (access_key == null || uid == null || change == null || tnonce == null || signature == null)
            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());

        Long tonceLong = Long.valueOf(tnonce) + 15000;
        if (new Date().getTime() > tonceLong)
            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());

        Access access = accessService.findByAccessKey(access_key);
        TreeMap<String, String> queryParas  = new TreeMap<>();
        queryParas.put("access_key", access_key);
        queryParas.put("uid", uid);
        queryParas.put("change", change);
        queryParas.put("tnonce", tnonce);
        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/updateBalance", access.getKey());

        if (!signature.equals(sign))
            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());

        User user = userService.findByUserId(Integer.valueOf(uid));

        JSONObject json = CoinApi.updateBalance(user.getSystemId(),change);

        Log log = new Log();
        log.setUid(Integer.valueOf(uid));
        log.setName(uid+"操作资产，更改资产数额"+change);
        log.setType(2);
        logService.insertLog(log);

        JSONObject result = json.getJSONObject("result");

        String status = result.getString("status");

        if ("success".equals(status))
            return new JsonResult();
        else
            return new JsonResult(BizExceptionEnum.UPDATE_BALANCE_ERROR.getCode(), BizExceptionEnum.UPDATE_BALANCE_ERROR.getMessage());

    }


    /**
     * 添加用户关联信息
     * @param access_key
     * @param uid
     * @param tnonce
     * @param signature
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value="/addUser", method = RequestMethod.POST)
    public JsonResult addUser(String access_key, String uid, String tnonce, String signature){

        if (access_key == null || uid == null|| tnonce == null || signature == null)
            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());

        Long tonceLong = Long.valueOf(tnonce) + 15000;
        if (new Date().getTime() > tonceLong)
            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());

        Access access = accessService.findByAccessKey(access_key);
        TreeMap<String, String> queryParas  = new TreeMap<>();
        queryParas.put("access_key", access_key);
        queryParas.put("uid", uid);
        queryParas.put("tnonce", tnonce);
        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/addUser", access.getKey());

        if (!signature.equals(sign))
            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());

        User user = userService.findByUserId(Integer.valueOf(uid));

        if (user != null)
            return new JsonResult(BizExceptionEnum.ADD_USER_ERROR.getCode(), BizExceptionEnum.ADD_USER_ERROR.getMessage());

        User addUser = new User();
        addUser.setSystemId(Long.valueOf(uid+openid));
        addUser.setOpenId(Integer.valueOf(openid));
        addUser.setUserId(Integer.valueOf(uid));
        userService.inserUser(addUser);

        Log log = new Log();
        log.setUid(Integer.valueOf(uid));
        log.setName("新增用户id："+uid);
        log.setType(1);
        logService.insertLog(log);

        return new JsonResult();

    }


    /**
     * 查询所有用户资产
     * @param access_key
     * @param tnonce
     * @param signature
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value="/getUserAllBalance", method = RequestMethod.POST)
    public JsonResult addUser(String access_key, String tnonce, String signature, Integer page) throws Exception {

        if (access_key == null || tnonce == null || signature == null)
            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getCode(), BizExceptionEnum.PARAMETER_CANT_BE_EMPTY.getMessage());

        Long tonceLong = Long.valueOf(tnonce) + 15000;
        if (new Date().getTime() > tonceLong)
            return new JsonResult(BizExceptionEnum.API_TIME_OUT_ERROR.getCode(), BizExceptionEnum.API_TIME_OUT_ERROR.getMessage());

        Access access = accessService.findByAccessKey(access_key);
        TreeMap<String, String> queryParas  = new TreeMap<>();
        queryParas.put("access_key", access_key);
        queryParas.put("tnonce", tnonce);
        String sign = EncryptUtil.sha256_HMAC(queryParas ,"/api/getUserAllBalance", access.getKey());

        if (!signature.equals(sign))
            return new JsonResult(BizExceptionEnum.API_SIGN_ERROR.getCode(), BizExceptionEnum.API_SIGN_ERROR.getMessage());


        page = page - 1;
        Integer limit = 10;
        Integer offset = page * limit;

        Map<String,Object> params = Maps.newHashMap();
        params.put("offset",offset);
        params.put("limit",limit);

        long total = userService.getCount();
        List<User> userList = userService.findAllList(params);
        List<UserAssetsVo> list = new ArrayList();

        for (User user: userList) {
            JSONObject json = CoinApi.queryBalance(user.getSystemId());
            JSONObject result = json.getJSONObject("result");
            JSONObject coin = result.getJSONObject("SIMI");
            String available = coin.getString("available");
            UserAssetsVo userVo = new UserAssetsVo();
            userVo.setUid(user.getUserId());
            userVo.setBalance(available);
            list.add(userVo);
        }

        long size;
        if (total % limit == 0){
            size = total/limit;
        } else {
            size = total/limit + 1;
        }

        return new JsonResult().addData("list",list).addData("total",String.valueOf(total)).addData("size",String.valueOf(size));

    }

}

