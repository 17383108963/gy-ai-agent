package com.gy.gyaiagent.service;

import com.gy.gyaiagent.model.dto.UserLoginRequest;
import com.gy.gyaiagent.model.dto.UserRegisterRequest;
import com.gy.gyaiagent.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gy.gyaiagent.model.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 郭谕耀
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-05-28 11:21:41
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 加密密码
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    UserLoginVO userLogin(UserLoginRequest userLoginRequest , HttpServletRequest request);

    /**
     * User对象转换VO
     * @param user
     * @return
     */
    UserLoginVO getUserLoginVO(User user);

    /**
     * 得到用户登录信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户退出
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);

}
