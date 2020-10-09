package com.springboot_activiti.BusinessMapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName BusineMapper.java
 * @Description TODO
 * @createTime 2020年05月16日 14:14:00
 */
@Component
@Mapper
public interface BusineMapper {

    // todo 登录
    @Select(value = "select * from activiti_account where username=#{username} and password=#{password} and yxbz=1")
    List<Map> getAccountByPassword(@Param("username") String username, @Param("password") String password);

    //登录时更新账号表的sessionid字段
    @Update(value = "update activiti_account set sessionid=#{sessionid} where username=#{username}")
    int updateSessionIdByUsername(@Param("sessionid") String sessionid,@Param("username") String username);

    //轮询检查sessionid是否存在或是否被替换
    @Select(value = "select * from activiti_account where sessionid=#{sessionid}")
    Map checkSessionId(String sessionid);

    @Insert(value = "insert into share values(#{id},#{url},#{pwd},#{kind},#{name},#{time})")
    int insertShare(Map map);

    @Delete(value = "delete from share where id=#{id}")
    int delShare(String id);

    //视频资源
    @Select(value = "select * from share where kind='视频资源'")
    List<Map> spzyList();
    //实用软件
    @Select(value = "select * from share where kind='实用软件'")
    List<Map> syrjList();
    //电影
    @Select(value = "select * from share where kind='电影'")
    List<Map> dyList();
    //其他
    @Select(value = "select * from share where kind='其他'")
    List<Map> qtList();
}
