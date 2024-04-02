package com.example.chatbot.bot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.chatbot.bot.entity.User;
import com.example.chatbot.bot.mapper.UserMapper;
import com.example.chatbot.bot.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    @Resource
    UserMapper userMapper;

    Cache<Long, Optional<User>> userCache = CacheBuilder.newBuilder()
            .maximumSize(100).expireAfterAccess(10, TimeUnit.MINUTES).build();

    @Override
    public boolean isVipUser(Long telegramId) {
        try {
            Optional<User> optional = userCache.get(telegramId, () -> {
                User user = queryUserById(telegramId);
                return Optional.of(user);
            });
            if(optional == null){
                return false;
            }
            if(optional.orElse(null) == null){
                return false;
            }
            return true;
        } catch (ExecutionException e) {
            return queryUserById(telegramId) != null;
        }
    }



    public User queryUserById(Long telegramId){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUser,telegramId);
        List<User> users = userMapper.selectList(wrapper);
        if(CollectionUtils.isEmpty(users)){
            return null;
        }
        return users.get(0);
    }
}
