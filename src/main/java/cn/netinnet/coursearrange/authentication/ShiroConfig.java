package cn.netinnet.coursearrange.authentication;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(ShiroConfig.class);


    /**
     * 创建自定义的验证规则
     *
     * @return
     */
    @Bean
    public ShiroRealm shiroRealm() {
        return new ShiroRealm();
    }


    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 去掉shiro登录时url里的JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 创建安全管理
     * 注意创建实现了web的对象
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置 SecurityManager，并注入 shiroRealm（与安全数据交互，即用户信息/权限/角色等数据的来源）
        securityManager.setRealm(shiroRealm());
        // 关闭shiro自带的session
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    /**
     * 创建shiro的过滤器,定义过滤规则
     *
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);


        //拦截到没有登录后跳到哪里去登录
        shiroFilterFactoryBean.setLoginUrl("/login");

        //拦截没有权限的用户跳到哪里去
        shiroFilterFactoryBean.setUnauthorizedUrl("/login");

        Map<String, String> authMap = new LinkedHashMap<>();

        //登录退出页面不拦截,其他全部拦截并进入jwt过滤器
        authMap.put("/login", "anon");
        authMap.put("/login/**", "anon");
        authMap.put("/logout", "logout");
        authMap.put("/welcome", "anon");

        authMap.put("/favicon.ico", "anon");
        authMap.put("/bootstrap/**", "anon");
        authMap.put("/css/**", "anon");
        authMap.put("/js/**", "anon");
        authMap.put("/img/**", "anon");

        // 在 Shiro过滤器链上加入 JWTFilter
        LinkedHashMap<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("jwt", new JWTFilter());

        shiroFilterFactoryBean.setFilters(filterMap);

        authMap.put("/**", "jwt,authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(authMap);
        LOGGER.info("-----shiro factory创建成功-----");
        return shiroFilterFactoryBean;
    }


    // 加入注解的使用，不加入这个注解不生效
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

}
