package com.chester.svc.sys.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.repository.RoleRepository;
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
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @PostMapping
    @Roles(value = "admin", remark = "添加角色", modify = false)
    public void addRole(@RequestBody Role role) {
        roleRepository.save(role);
    }

    @DeleteMapping
    @Roles(value = "admin", remark = "删除角色", modify = false)
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

}
