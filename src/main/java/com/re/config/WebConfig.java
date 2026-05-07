package com.re.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Chỉ áp dụng bộ lọc cho các đường dẫn nhạy cảm
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/lecturer/**", "/admin/**", "/student/**")
                .excludePathPatterns("/auth/**", "/css/**", "/js/**");
    }
}
