package com.chester.svc.sys.web.controller;

import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.db.repository.MenuRepository;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.svc.sys.util.UserUtils;
import com.chester.util.coll.Lists;
import com.chester.util.tree.ListTreeSource;
import com.chester.util.tree.TreeBuilder;
import com.chester.util.tree.TreeNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/sys/menus")
public class MenuController {
    @Resource
    private MenuRepository menuRepository;
    @Resource
    private UserRepository userRepository;

    @GetMapping
    @Roles(value = "admin", remark = "获取所有菜单列表", modify = false)
    public List<Menu> find() {
        return menuRepository.findAll();
    }

    @GetMapping("/user")
    @Roles(value = "authed", remark = "获取用户菜单树", modify = false)
    public TreeNode<Menu> getMenus() {
        User user = userRepository.getOne(Objects.requireNonNull(UserUtils.getUserId()));
        List<Menu> list = new ArrayList<>();
        List<Role> roles = Stream.of(user.getRoles().toArray(new Role[]{})).collect(Collectors.toList());
        for (Set<Menu> _list : Lists.map(roles, Role::getMenus)) {
            list.addAll(_list);
        }
        List<Menu> _list = Lists.filter(find(), v -> v.getParentIds() == null);
        list.addAll(_list);
        return getNode(list.stream().distinct().collect(Collectors.toList()));
    }

    @GetMapping("/modify")
    @Roles(value = "admin", remark = "获取可设置的用户菜单树", modify = false)
    public TreeNode<Menu> getModifyMenus() {
        List<Menu> list = find();
        return getNode(list);
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
