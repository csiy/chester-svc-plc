package com.chester.svc.sys.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_role")
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String name;
    private String remark;
    private Boolean modify = false;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Menu> menus;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Rule> rules;
}
