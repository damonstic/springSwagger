package com.example.springswagger.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springswagger.entity.User;
import com.example.springswagger.mapper.UserMapper;
import com.example.springswagger.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UseServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Override
    public void deductBalance(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == 2) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额
        //baseMapper.deductMoneyById(id, money);
        // 4.扣减余额 update tb_user set balance = balance - ?
        int remainBalance = user.getBalance() - money;
        lambdaUpdate()
                .set(User::getBalance, remainBalance) // 更新余额
                .set(remainBalance == 0, User::getStatus, 2) // 动态判断，是否更新status
                .eq(User::getId, id)
                .eq(User::getBalance, user.getBalance()) // 乐观锁
                .update();
    }

    @Override
    public List<User> queryUsers(String name, Integer status, Integer maxBalance, Integer minBalance) {

        return lambdaQuery().like(name!= null,User::getUsername,name)
                            .eq(status!=null,User::getStatus,status)
                            .ge(minBalance != null, User::getBalance, minBalance)
                            .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
    }
}
