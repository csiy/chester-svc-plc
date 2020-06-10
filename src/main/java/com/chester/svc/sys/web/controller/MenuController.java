package com.chester.svc.sys.web.controller;

import com.chester.auth.client.Roles;
import com.chester.auth.client.UserTokenHolder;
import com.chester.svc.sys.mongodb.repository.MenuRepository;
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
    @Roles(value = "admin",remark = "获取所有菜单列表",modify = false)
    public List<ResMenu> find(){
        return menuRepository.findMenu();
    }

    @GetMapping("/user")
    @Roles(value = "authed",remark = "获取用户菜单树",modify = false)
    public TreeNode<ResMenu> getMenus(){
        List<ResMenu> list = menuRepository.findMenu(UserTokenHolder.getRoles());
        ListTreeSource<String,ResMenu> treeSource = new ListTreeSource<String,ResMenu>(list,ResMenu::getMenuId,v->{
            if(!Lists.isEmpty(v.getParentIds())){
                return v.getParentIds().get(v.getParentIds().size()-1);
            }
            return "root";
        },ResMenu::getSort);
        return TreeBuilder.build(treeSource, v->{
            ResMenu rootMenu = new ResMenu();
            rootMenu.setMenuId("root");
            return rootMenu;
        });
    }

    @PutMapping
    @Roles(value = "admin",remark = "设置菜单角色",modify = false)
    public void updateMenu(@RequestBody ReqMenuUpdate menu){
        menuRepository.updateMenu(menu,UserTokenHolder.getUserId());
    }

    @GetMapping("/modify")
    @Roles(value = "admin",remark = "获取可设置的用户菜单树",modify = false)
    public TreeNode<ResMenu> getModifyMenus(){
        List<ResMenu> list = find();
        ListTreeSource<String,ResMenu> treeSource = new ListTreeSource<>(list,ResMenu::getMenuId, v->{
            if(!Lists.isEmpty(v.getParentIds())){
                return v.getParentIds().get(v.getParentIds().size()-1);
            }
            return "root";
        },ResMenu::getSort);
        return TreeBuilder.build(treeSource, v->{
            ResMenu rootMenu = new ResMenu();
            rootMenu.setMenuId("root");
            return rootMenu;
        });
    }

    @PutMapping("/pull")
    @Roles(value = "admin",remark = "删除一个角色的某个菜单",modify = false)
    public void pullMenu(@RequestBody ReqMenu menu){
        menuRepository.pullMenu(menu,UserTokenHolder.getUserId());
    }

    @PutMapping("/push")
    @Roles(value = "admin",remark = "添加一个角色的某个菜单",modify = false)
    public void pushMenu(@RequestBody ReqMenu menu){
        menuRepository.pushMenu(menu,UserTokenHolder.getUserId());
    }

}
