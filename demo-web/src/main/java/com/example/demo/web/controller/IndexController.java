package com.example.demo.web.controller;

import com.example.demo.common.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController extends BaseController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/api")
    public String api() {
        return "index";
    }
}
