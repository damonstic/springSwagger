package com.example.springswagger.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String info;
    private Integer status;
    private Integer balance;
    private String createTime;
    private String updateTime;


}
