package com.chester.svc.sys.mongodb.repository;

import com.chester.svc.sys.mongodb.model.Role;
import com.chester.svc.sys.web.model.res.ResRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepository {

    public void addRole(Role role, Long createdBy) {
    }

    public void deleteRole(Long roleId, Long updatedBy) {
    }

    public List<ResRole> findRoles() {
        return null;
    }
}
