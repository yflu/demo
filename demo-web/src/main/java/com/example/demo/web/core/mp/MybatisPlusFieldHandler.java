package com.example.demo.web.core.mp;

import com.example.demo.core.config.CustomMetaObjectHandler;
import com.example.demo.web.core.shiro.util.ShiroKit;
import org.springframework.stereotype.Component;

/**
 * 字段填充器
 */
@Component
public class MybatisPlusFieldHandler extends CustomMetaObjectHandler {

    @Override
    protected Object getUserId() {
        try {
            return ShiroKit.getUser().getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
