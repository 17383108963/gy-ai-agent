package com.gy.gyaiagent.controller;

import com.gy.gyaiagent.common.BaseResponse;
import com.gy.gyaiagent.common.ResultUtils;
import com.gy.gyaiagent.exception.ErrorCode;
import com.gy.gyaiagent.exception.ThrowUtils;
import com.gy.gyaiagent.model.dto.UserLoginRequest;
import com.gy.gyaiagent.model.dto.UserRegisterRequest;
import com.gy.gyaiagent.model.entity.User;
import com.gy.gyaiagent.model.vo.UserLoginVO;
import com.gy.gyaiagent.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author gyy
 * @version 1.1
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        long userId = userService.userRegister(userRegisterRequest);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        UserLoginVO userLoginVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(userLoginVO);
    }

    @GetMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserLoginVO(user));
    }

    @GetMapping("/logout")
    public BaseResponse<Boolean> userLogOut(HttpServletRequest request) {
        boolean result = userService.userLogOut(request);
        return ResultUtils.success(result);
    }
}
