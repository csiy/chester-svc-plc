package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plc/datas")
public class DataController {

    @GetMapping
    @Roles(value = "admin",remark = "查询数据")
    public void findDatas(){

    }

}
