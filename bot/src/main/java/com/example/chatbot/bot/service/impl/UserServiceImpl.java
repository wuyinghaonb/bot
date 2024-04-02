package com.example.chatbot.bot.service.impl;

import com.example.chatbot.bot.entity.User;
import com.example.chatbot.bot.mapper.UserMapper;
import com.example.chatbot.bot.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author  
 * @since 2024-04-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
