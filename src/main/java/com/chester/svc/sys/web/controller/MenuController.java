package com.chester.svc.sys.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.repository.MenuRepository;
import com.chester.svc.sys.web.model.req.ReqMenu;
import com.chester.svc.sys.web.model.req.ReqMenuUpdate;
import com.chester.svc.sys.web.model.res.ResMenu;
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
        List<ResMenu> list = menuRepository.findMenu(UserTokenHolder.getRoles());
        return getNode(list);
    }

    @PutMapping
    @Roles(value = "admin", remark = "设置菜单角色", modify = false)
    public void updateMenu(@RequestBody ReqMenuUpdate menu) {
        menuRepository.updateMenu(menu, UserTokenHolder.getUserId());
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
        menuRepository.pullMenu(menu, UserTokenHolder.getUserId());
    }

    @PutMapping("/push")
    @Roles(value = "admin", remark = "添加一个角色的某个菜单", modify = false)
    public void pushMenu(@RequestBody ReqMenu menu) {
        menuRepository.pushMenu(menu, UserTokenHolder.getUserId());
    }

    private TreeNode<Menu> getNode(List<Menu> list) {
        ListTreeSource<String, Menu> treeSource = new ListTreeSource<>(list, Menu::getKey, v -> {
            if (v.getParentIds()!=null) {
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
