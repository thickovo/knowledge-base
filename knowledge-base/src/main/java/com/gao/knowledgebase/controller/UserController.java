package com.gao.knowledgebase.controller;

import com.gao.knowledgebase.common.Result;
import com.gao.knowledgebase.dto.UserInfoDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.gao.knowledgebase.dto.LoginRequest;
import com.gao.knowledgebase.entity.User;
import com.gao.knowledgebase.service.UserService;
import com.gao.knowledgebase.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/test")
    public Result<String> test(){
        return Result.success("ok");
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody LoginRequest loginRequest) {
        //检查用户名是否存在
        User exisUser = userService.lambdaQuery()
                .eq(User::getUsername, loginRequest.getUsername())
                .one();
        if (exisUser != null) {
            return Result.error("用户已存在");
        }
        //创建新用户
        User user = new User();
        user.setUsername(loginRequest.getUsername());
        user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        //保存到数据库
        userService.save(user);
        //返回成功
        return Result.success("注册ok");
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        User uesr = userService.lambdaQuery()
                .eq(User::getUsername, loginRequest.getUsername())
                .one();
        if (uesr == null) {
            return Result.error("用户不存在");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(),uesr.getPassword())){
            return Result.error("密码错误");
        }
        String token = jwtUtils.generateToken(uesr.getUsername());
        return Result.success(token);
    }

    @GetMapping("/me")
    public Result<UserInfoDTO> getCurrentUser(){
        //1.从Security上下文获取用户名
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        //2.用userService查询用户
        User user = userService.lambdaQuery().eq(User::getUsername,username).one();
        if (user == null) {
            return Result.error("用户不存在");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user,userInfoDTO);
        //3.返回
        return Result.success(userInfoDTO);
    }
}
