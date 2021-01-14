package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.db.model.Material;
import com.chester.svc.plc.db.repository.MaterialRepository;
import com.chester.svc.plc.web.model.req.ReqPageMaterial;
import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.util.UserUtils;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/plc/materials")
public class MaterialController {

    @Resource
    private MaterialRepository materialRepository;

    @PostMapping
    @Roles(value = "admin,operator", remark = "添加物料")
    public void addMaterial(@RequestBody Material material) {
    }

    @PutMapping
    @Roles(value = "admin,operator", remark = "修改物料")
    public void putMaterial(@RequestBody Material material) {
    }

    @GetMapping("/{materialCode}/{aoCode}")
    @Roles(value = "admin,operator", remark = "获取物料")
    public Material getMaterial(@PathVariable("materialCode") String materialCode, @PathVariable("aoCode") String aoCode) {
        return null;
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找物料")
    public PageResult<Material> findMaterial(ReqPageMaterial reqPageMaterial, Pagination pagination) {
        return null;
    }

    @DeleteMapping("/{materialId}/{version}")
    @Roles(value = "admin,operator", remark = "删除物料")
    public void deleteMaterial(@PathVariable("materialId") String materialId, @PathVariable("version") Integer version) {
    }
}
