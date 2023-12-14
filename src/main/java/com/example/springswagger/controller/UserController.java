package com.example.springswagger.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springswagger.domain.dto.UserFormDTO;
import com.example.springswagger.domain.query.UserQuery;
import com.example.springswagger.domain.vo.UserVO;
import com.example.springswagger.entity.User;
import com.example.springswagger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor//必备构造函数
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary ="新增用户")
    public void saveUser(@RequestBody UserFormDTO userFormDTO){
        // 1.转换DTO为PO
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void removeUserById(@PathVariable("id") Long userId){
        userService.removeById(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户")
        public UserVO queryUserById(@PathVariable("id") Long userId){
        // 1.查询用户
        Object user = userService.getById(userId);
        // 2.处理vo
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @GetMapping
    @Operation(summary = "根据id集合查询用户")
    public List<UserVO> queryUserByIds(@RequestParam("ids") List<Long> ids){
        // 1.查询用户
        List<User> users = userService.listByIds(ids);
        // 2.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

//根据id扣减用户余额
// 这看起来是个简单修改功能，只要修改用户余额即可。但这个业务包含一些业务逻辑处理：
//- 判断用户状态是否正常
//- 判断用户余额是否充足
    @PutMapping("{id}/deduction/{money}")
    @Operation(summary = "扣减用户余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money")Integer money){
        userService.deductBalance(id, money);
    }

//实现一个根据复杂条件查询用户的接口，查询条件如下：
//- name：用户名关键字，可以为空
//- status：用户状态，可以为空
//- minBalance：最小余额，可以为空
//- maxBalance：最大余额，可以为空
    @GetMapping("/list1")
    @Operation(summary = "多条件查询用户-基于QueryWrapper")
    public List<UserVO> queryUsers(UserQuery query){
        // 1.组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance);
        // 2.查询用户
        List<User> users = userService.list(wrapper);
        // 3.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @GetMapping("/list2")
    @Operation(summary = "多条件查询用户-基于lambdaQuery")
    public List<UserVO> queryUsersByLamdba(UserQuery query){
        // 1.组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();
        // 2.查询用户
        List<User> users = userService.lambdaQuery()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();//one()：最多1个结果,list()：返回集合结果,count()：返回计数结果
        // 3.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }

    @GetMapping("/list3")
    @Operation(summary = "多条件查询用户-自定义查询")
    public List<UserVO> queryUsersByCondition(UserQuery query){
        List<User> list = userService.queryUsers(query.getName(),query.getStatus(),query.getMaxBalance(),query.getMinBalance());
        return BeanUtil.copyToList(list,UserVO.class);
    }

}
