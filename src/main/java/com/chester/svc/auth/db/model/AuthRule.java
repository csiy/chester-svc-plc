package com.chester.svc.auth.db.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude={"path","type"}, callSuper = false)
@Entity
@Table(name = "plc_rule")
public class AuthRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;
    private String path;
    private String roles;
    private String type;
    private String remark;
    private Boolean modify = true;

}
