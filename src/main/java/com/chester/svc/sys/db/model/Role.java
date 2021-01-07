package com.chester.svc.sys.db.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "plc_role")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String name;
    private String remark;
    private Boolean modify = false;
}
