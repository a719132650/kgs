package com.kigooo.kgs.component.kgMvc;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class KgMvcConfig implements WebMvcConfigurer {
    @Autowired
    private KgMvcIntercept1 kgMvcIntercept1;

    /**
     * 自定义拦截器应用配置
     * @param registry 拦截器注册类
     * @return void
     * @author
     * @date 2021/8/30 13:09
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(kgMvcIntercept1);
    }

}
