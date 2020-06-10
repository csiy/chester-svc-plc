package com.chester.svc.sys.web.controller;

import com.chester.auth.client.Roles;
import com.chester.auth.client.UserTokenHolder;
import com.chester.svc.sys.mongodb.model.User;
import com.chester.svc.sys.mongodb.repository.MenuRepository;
import com.chester.svc.sys.mongodb.repository.RoleRepository;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResInit;
import com.chester.svc.sys.web.model.res.ResMenu;
import com.chester.svc.sys.web.model.res.ResRole;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.coll.Lists;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.chester.util.tree.ListTreeSource;
import com.chester.util.tree.TreeBuilder;
import com.chester.util.tree.TreeNode;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.tree.Tree;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sys/users")
public class UserController {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MenuController menuController;
    @Resource
    private RoleController roleController;

    @GetMapping("/init")
    @Roles(value = "authed",remark = "获取用户信息",modify = false)
    public ResInit getUser() {
        ResInit init = new ResInit();
        ResUser user = userRepository.getUser(UserTokenHolder.getUserId());
        init.setUser(user);

        init.setMenus(menuController.getMenus());
        init.setRoles(roleController.getRoles());
        return init;
    }

    @GetMapping("/pages")
    @Roles(value = "admin",remark = "获取用户分页数据",modify = false)
    public PageResult<ResUser> findUser(ReqQueryUser query, Pagination pagination){
        return userRepository.find(query,pagination);
    }

    @PostMapping("/user")
    @Roles(value = "admin",remark = "添加用户",modify = false)
    public void addUser(@RequestBody User user){
        user.setCreatedBy(UserTokenHolder.getUserId());
        userRepository.add(user);
    }

    @PutMapping("/major")
    @Roles(value = "admin",remark = "修改用户权限，手机，姓名",modify = false)
    public void updateUser(@RequestBody User user){
        userRepository.update(user,UserTokenHolder.getUserId());
    }

    @PutMapping("/password")
    @Roles(value = "admin",remark = "重置用户密码",modify = false)
    public String reSetPassword(@RequestBody User user){
        return userRepository.reSetPassword(user.getUserId(),UserTokenHolder.getUserId());
    }

    @PutMapping("/info")
    @Roles(value = "authed",remark = "修改用户基本信息",modify = false)
    public ResUser updateUser(@RequestBody ReqUpdateUserInfo user){
        userRepository.updateUserInfo(user,UserTokenHolder.getUserId());
        return userRepository.getUser(UserTokenHolder.getUserId());
    }

    @PutMapping("/disabled")
    @Roles(value = "admin",remark = "启用或禁用用户",modify = false)
    public void switchDisabled(@RequestBody User user){
        userRepository.switchDisabled(user.getUserId(),UserTokenHolder.getUserId());
    }

    @DeleteMapping("/remove")
    @Roles(value = "admin",remark = "删除",modify = false)
    public void deleteUser(User user){
        userRepository.deleteUser(user.getUserId(),UserTokenHolder.getUserId());
    }

}
