package com.chester.svc.auth.db.repository;

import com.chester.svc.auth.db.model.AuthRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public
interface AuthRuleDao extends JpaRepository<AuthRule, Long> {

}
