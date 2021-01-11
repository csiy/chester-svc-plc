package com.chester.svc.sys.web.model.res;

import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.db.model.Role;
import com.chester.svc.sys.db.model.User;
import com.chester.util.tree.TreeNode;
import lombok.Data;

import java.util.List;

@Data
public class ResInit {
    private User user;
    private List<Role> roles;
    private TreeNode<Menu> menus;
}
