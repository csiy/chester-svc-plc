package com.chester.svc.sys.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.sys.mongodb.model.Role;
import com.chester.svc.sys.mongodb.repository.RoleRepository;
import com.chester.svc.sys.web.model.res.ResRole;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sys/roles")
public class RoleController {
    @Resource
    private RoleRepository roleRepository;

    @GetMapping
    @Roles(value = "authed", remark = "获取所有角色列表", modify = false)
    public List<ResRole> getRoles() {
        return roleRepository.findRoles();
    }

    @PostMapping
    @Roles(value = "admin", remark = "添加角色", modify = false)
    public void addRole(@RequestBody Role role) {
        roleRepository.addRole(role, UserTokenHolder.getUserId());
    }

    @DeleteMapping
    @Roles(value = "admin", remark = "删除角色，并不能级联删除", modify = false)
    public void deleteRole(Long roleId) {
        roleRepository.deleteRole(roleId, UserTokenHolder.getUserId());
    }

}
