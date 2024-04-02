package com.example.chatbot.bot.service;

import com.example.chatbot.bot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2024-04-02
 */
public interface UserService extends IService<User> {

    /**
     * 判断是否为vip用户
     * @param telegramId
     * @return
     */
    boolean isVipUser(Long telegramId);

}
