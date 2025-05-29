package com.gy.gyaiagent.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gyy
 * @version 1.1
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

