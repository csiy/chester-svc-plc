package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
