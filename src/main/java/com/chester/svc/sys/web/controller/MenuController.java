package com.chester.svc.sys.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.repository.MenuRepository;
import com.chester.svc.sys.web.model.req.ReqMenu;
import com.chester.svc.sys.web.model.req.ReqMenuUpdate;
import com.chester.util.coll.Lists;
import com.chester.util.tree.ListTreeSource;
import com.chester.util.tree.TreeBuilder;
import com.chester.util.tree.TreeNode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/sys/menus")
public class MenuController {
    @Resource
    private MenuRepository menuRepository;

    @GetMapping
    @Roles(value = "admin", remark = "获取所有菜单列表", modify = false)
    public List<Menu> find() {
        return menuRepository.findAll();
    }

    @GetMapping("/user")
    @Roles(value = "authed", remark = "获取用户菜单树", modify = false)
    public TreeNode<Menu> getMenus() {
        List<Menu> list = menuRepository.findByRolesIn(UserTokenHolder.getRoles());
        return getNode(list);
    }

    @PutMapping
    @Roles(value = "admin", remark = "设置菜单角色", modify = false)
    public void updateMenu(@RequestBody ReqMenuUpdate menu) {
        Menu _menu = menuRepository.getOne(menu.getMenuId());
        _menu.setRoles(Lists.map(menu.getRoles(), v -> {
            Role role = new Role();
            role.setRoleId(v);
            return role;
        }));
        menuRepository.save(_menu);
    }

    @GetMapping("/modify")
    @Roles(value = "admin", remark = "获取可设置的用户菜单树", modify = false)
    public TreeNode<Menu> getModifyMenus() {
        List<Menu> list = find();
        return getNode(list);
    }

    @PutMapping("/pull")
    @Roles(value = "admin", remark = "删除一个角色的某个菜单", modify = false)
    public void pullMenu(@RequestBody ReqMenu menu) {
        Lists.each(menu.getMenuIds(), v -> {
            Menu _menu = menuRepository.getOne(v);
            Role role = new Role();
            role.setRoleId(menu.getRole());
            _menu.getRoles().remove(role);
            menuRepository.save(_menu);
        });
    }

    @PutMapping("/push")
    @Roles(value = "admin", remark = "添加一个角色的某个菜单", modify = false)
    public void pushMenu(@RequestBody ReqMenu menu) {
        Lists.each(menu.getMenuIds(), v -> {
            Menu _menu = menuRepository.getOne(v);
            Role role = new Role();
            role.setRoleId(menu.getRole());
            _menu.getRoles().add(role);
            menuRepository.save(_menu);
        });
    }

    private TreeNode<Menu> getNode(List<Menu> list) {
        ListTreeSource<String, Menu> treeSource = new ListTreeSource<>(list, Menu::getKey, v -> {
            if (v.getParentIds() != null) {
                return v.getParentIds();
            }
            return "root";
        }, Menu::getSort);
        return TreeBuilder.build(treeSource, v -> {
            Menu rootMenu = new Menu();
            rootMenu.setKey("root");
            return rootMenu;
        });
    }
}
