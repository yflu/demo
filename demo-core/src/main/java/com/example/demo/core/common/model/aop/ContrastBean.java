package com.example.demo.core.common.model.aop;

import lombok.Data;

@Data
public class ContrastBean {

    private String name;//字段名

    private Object ov;//旧值

    private Object nv;//新值

    public ContrastBean(String name, Object ov, Object nv) {
        this.name = name;
        this.ov = ov;
        this.nv = nv;
    }
}
