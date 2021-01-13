package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public
interface AuthRuleDao extends JpaRepository<Rule, Long> {

}
