package com.gy.gyaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gyy
 * @version 1.1
 * ReAct(Reasoning and Acting)的代理抽象类
 * 实现了思考-行动的循环模式
 */
@Data
@EqualsAndHashCode(callSuper=true)
public abstract class ReActAgent extends BaseAgent{

    /**
     * 处理当前状态并决定下一次行动
     * @return 是否需要执行行动
     */
    public abstract boolean think();

    /**
     * 执行行动
     * @return 行动结果
     */
    public abstract String act();

    /**
     * 执行单个步骤：思考和行动
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct){
                return "思考完成-无需行动";
            }
            return act();
        }catch (Exception e){
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败" + e.getMessage();
        }
    }
}
