package com.gy.gyaiagent.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gy.gyaiagent.constant.UserConstant;
import com.gy.gyaiagent.exception.BusinessException;
import com.gy.gyaiagent.exception.ErrorCode;
import com.gy.gyaiagent.model.dto.UserLoginRequest;
import com.gy.gyaiagent.model.dto.UserRegisterRequest;
import com.gy.gyaiagent.model.entity.User;
import com.gy.gyaiagent.model.enums.UserRoleEnum;
import com.gy.gyaiagent.model.vo.UserLoginVO;
import com.gy.gyaiagent.service.UserService;
import com.gy.gyaiagent.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author 郭谕耀
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-05-28 11:21:41
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        //1.参数校验
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空！");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名不能小于4位！");
        }
        if(userPassword.length() < 6){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能小于6位！");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致！");
        }
        //2.判断数据库是否存在相同账号
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账号已存在！");
        }
        //3.对密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean result = this.save(user);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库插入失败！");
        }
        //5.返回用户id
        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        //加盐，混淆密码
        final String salt = "gy";
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }

    @Override
    public UserLoginVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        //1.校验参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空！");
        }
        //2.加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        //3.去数据库查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>().eq("user_account", userAccount)
                .eq("user_password", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed!");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名不存在或密码错误，登陆失败！");
        }
        //4.保存登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        UserLoginVO userLoginVO = getUserLoginVO(user);
        return userLoginVO;
    }

    @Override
    public UserLoginVO getUserLoginVO(User user) {
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        return userLoginVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public boolean userLogOut(HttpServletRequest request) {
        //1.判断用户是否登录
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        //2.退出登录，取消登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

}




