package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openid获得用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openId);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 查询某个时间段的用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}