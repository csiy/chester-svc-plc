package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User getByKeyOrPhone(String key,String phone);
}
