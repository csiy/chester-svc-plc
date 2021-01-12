package com.chester.svc.sys.db.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "plc_menu")
@AllArgsConstructor
@NoArgsConstructor
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
    private Boolean modify = true;
    private String parentIds;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
