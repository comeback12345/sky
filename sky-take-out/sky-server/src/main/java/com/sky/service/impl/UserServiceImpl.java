package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LPGIN ="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties wxproperties;
    @Autowired
    private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openId = getOpenid(userLoginDTO.getCode());
        //判断是否为空，为空表示登录失败

        if(openId==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        User user = userMapper.getByOpenId(openId);

        //新用户完成注册
        if(user==null){
           user = User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
        }
        userMapper.insert(user);
        return user;
    }

    private String getOpenid(String code){
        //调用微信服务，获取当前微信用户的openid
        Map<String,String> map=new HashMap<>();
        map.put("appid",wxproperties.getAppid());
        map.put("secret",wxproperties.getSecret());
        map.put("grand_type","authorization_code");
        map.put("js_code",code);
        String json= HttpClientUtil.doGet(WX_LPGIN,map);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openId=jsonObject.getString("openid");
        return openId;
    }
}
