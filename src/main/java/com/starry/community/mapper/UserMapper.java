package com.starry.community.mapper;

import com.starry.community.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Starry
 * @create 2022-09-01-5:05 PM
 * @Describe
 */
@Mapper
public interface UserMapper {
    /**
     *
     * @param id
     * @return
     */
    User selectById(int id);

    /**
     *
     * @param username
     * @return
     */
    User selectByUsername(String username);

    /**
     *
     * @param email
     * @return
     */
    User selectByEmail(String email);

    /**
     *
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     *
     * @param id
     * @param status
     * @return
     */
    int updateStatusById(int id, int status);

    /**
     *
     * @param id
     * @param headerUrl
     * @return
     */
    int updateHeaderById(int id, String headerUrl);

    /**
     *
     * @param id
     * @param password
     * @return
     */
    int updatePasswordById(int id, String password);
}
