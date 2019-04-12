package com.example.demo.web.core.beetl;

import com.example.demo.core.util.ToolUtil;
import com.example.demo.web.core.kaptcha.KaptchaUtil;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.web.core.common.Const.DEFAULT_SYSTEM_NAME;
import static com.example.demo.web.core.common.Const.DEFAULT_WELCOME_TIP;

@Configuration
public class BeetlConf {

    @Bean(initMethod = "init", name = "beetlConfig")
    public BeetlConfiguration getBeetlGroupUtilConfiguration() {
        BeetlConfiguration beetlGroupUtilConfiguration = new BeetlConfiguration();
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader();
        beetlGroupUtilConfiguration.setResourceLoader(classpathResourceLoader);
        return beetlGroupUtilConfiguration;
    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }

    public class BeetlConfiguration extends BeetlGroupUtilConfiguration {

        @Autowired
        private Environment env;

        @Value("${customize.resources.version}")
        private String version;

        @Override
        public void initOther() {
            groupTemplate.registerFunctionPackage("shiro", new ShiroExt());
            groupTemplate.registerFunctionPackage("tool", new ToolUtil());
            groupTemplate.registerFunctionPackage("kaptcha", new KaptchaUtil());

            Map<String, Object> shared = new HashMap();
            shared.put("version", version);
            shared.put("systemName", DEFAULT_SYSTEM_NAME);
            shared.put("welcomeTip", DEFAULT_WELCOME_TIP);
            groupTemplate.setSharedVars(shared);
        }
    }
}
