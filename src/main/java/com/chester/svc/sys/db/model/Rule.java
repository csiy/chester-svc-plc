package com.chester.svc.sys.db.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = {"path", "type"}, callSuper = false)
@Entity
@Table(name = "plc_rule")
public class Rule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;
    private String path;
    private String type;
    private String initRole;
    private String remark;
    private Boolean modify = true;

}
