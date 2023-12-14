package com.example.springswagger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springswagger.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    void deductBalance(Long id, Integer money);

    List<User> queryUsers(String name, Integer status, Integer maxBalance, Integer minBalance);
}
