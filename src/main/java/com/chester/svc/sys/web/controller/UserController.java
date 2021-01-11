package com.chester.svc.sys.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResInit;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/sys/users")
public class UserController {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MenuController menuController;
    @Resource
    private RoleController roleController;
    @Resource
    private PasswordEncoder passwordEncoder;

    @GetMapping("/init")
    @Roles(value = "authed", remark = "获取用户信息", modify = false)
    public ResInit getUser() {
        ResInit init = new ResInit();
        User user = userRepository.getOne(Objects.requireNonNull(UserTokenHolder.getUserId()));
        init.setUser(user);
        init.setMenus(menuController.getMenus());
        init.setRoles(roleController.getRoles());
        return init;
    }

    @GetMapping("/pages")
    @Roles(value = "admin", remark = "获取用户分页数据", modify = false)
    public Page<User> findUser(User query, PageRequest pageable) {
        Example<User> example = Example.of(query);
        return userRepository.findAll(example,pageable);
    }

    @PostMapping("/user")
    @Roles(value = "admin", remark = "添加用户", modify = false)
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @PutMapping("/major")
    @Roles(value = "admin", remark = "修改用户权限，手机，姓名", modify = false)
    public void updateUser(@RequestBody User user) {
        User _user = userRepository.getOne(user.getUserId());
        if(!StringUtils.isEmpty(user.getName())){
            _user.setName(user.getName());
        }
        if(!StringUtils.isEmpty(user.getPhone())){
            _user.setPhone(user.getPhone());
        }
        if(!StringUtils.isEmpty(user.getRoles())){
            _user.setRoles(user.getRoles());
        }
        userRepository.save(_user);
    }

    @PutMapping("/password")
    @Roles(value = "admin", remark = "重置用户密码", modify = false)
    public String reSetPassword(@RequestBody User user) {
        String newPassword = (new Random().nextInt(899999) + 100000) + "";
        String pwd = passwordEncoder.encode(newPassword);
        User _user = userRepository.getOne(user.getUserId());
        _user.setPassword(pwd);
        userRepository.save(_user);
        return newPassword;
    }

    @PutMapping("/info")
    @Roles(value = "authed", remark = "修改用户基本信息", modify = false)
    public User updateUser(@RequestBody ReqUpdateUserInfo user) {
        Long userId = UserTokenHolder.getUserId();
        User _user = userRepository.getOne(Objects.requireNonNull(userId));
        if(!StringUtils.isEmpty(user.getSex())){
            _user.setSex(user.getSex());
        }
        if(!StringUtils.isEmpty(user.getPhoto())){
            _user.setPhoto(user.getPhoto());
        }
        if(!StringUtils.isEmpty(user.getBirthday())){
            _user.setBirthday(user.getBirthday());
        }
        userRepository.save(_user);
        return userRepository.getOne(Objects.requireNonNull(userId));
    }

    @PutMapping("/disabled")
    @Roles(value = "admin", remark = "启用或禁用用户", modify = false)
    public void switchDisabled(@RequestBody User user) {
        User _user = userRepository.getOne(user.getUserId());
        _user.setIsDisabled(!_user.getIsDisabled());
        userRepository.save(_user);
    }

    @DeleteMapping("/remove")
    @Roles(value = "admin", remark = "删除", modify = false)
    public void deleteUser(User user) {
        userRepository.deleteById(user.getUserId());
    }

}
