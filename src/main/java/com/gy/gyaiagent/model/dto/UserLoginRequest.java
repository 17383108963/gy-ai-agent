package com.gy.gyaiagent.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gyy
 * @version 1.1
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

}
