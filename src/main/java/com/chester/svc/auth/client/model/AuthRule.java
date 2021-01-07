package com.chester.svc.auth.client.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "plc_role")
public class AuthRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;
    String path;
    List<String> roles;
    String type;
    String remark;
    Boolean modify = true;
}
