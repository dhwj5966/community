package com.starry.community.mapper;

import com.starry.community.bean.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author Starry
 * @create 2022-09-05-1:08 PM
 * @Describe
 */
@Mapper
public interface LoginTicketMapper {

    /**
     *
     */
    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) " +
                    "values (#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
//自动生成的主键id的值，注入到loginTicket对象名为id的field中
    int insertLoginTicket(LoginTicket loginTicket);


    @Select({
            "select id, user_id, ticket, status, expired from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 把指定ticket的LoginTicket的status修改为targetStatus
     */
    @Update({
            "update login_ticket set status=#{targetStatus} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int targetStatus);
}
