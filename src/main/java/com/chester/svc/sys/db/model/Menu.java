package com.chester.svc.sys.db.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "plc_menu")
public class Menu extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;
    private String key;
    private String name;
    private String icon;
    private String url;
    private String path;
    private Integer sort;
    private String type;
    private Boolean modify = true;
    private String parentIds;
    private String roles;
}
