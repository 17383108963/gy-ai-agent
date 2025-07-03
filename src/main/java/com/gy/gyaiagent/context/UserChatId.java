package com.gy.gyaiagent.context;

import lombok.experimental.UtilityClass;

public class UserChatId implements AutoCloseable {

    private static final ThreadLocal<String> ctx = new ThreadLocal<>();

    public static void setCtx(String ChatId) {
        ctx.set(ChatId);
    }

    public static String getCtx() {
        return ctx.get();
    }

    @Override
    public void close() {
        ctx.remove();
    }
}
