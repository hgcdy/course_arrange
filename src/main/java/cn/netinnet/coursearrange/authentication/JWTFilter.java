package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.CourseArrangeApplication;
import lombok.SneakyThrows;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends BasicHttpAuthenticationFilter {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseArrangeApplication.class);

    // 后端免认证接口 url
    public static String anonUrl;
    // 静态变量通过set方法注入
    @Value("${project.shiro.anonUrl}")
    public void setAnonUrl(String anonUrl) {
        JWTFilter.anonUrl = anonUrl;
    }
    // 匹配url路径
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @SneakyThrows
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        //获取token
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String jwtToken = JWTUtil.getToken(httpRequest);

        String url = httpRequest.getServletPath();//url
        String host = httpRequest.getRemoteHost();//主机名
        int port = httpRequest.getRemotePort();//端口号

        String format = "请求用户: %s:%s, 请求接口: %s";
        LOGGER.info(String.format(format, ("0:0:0:0:0:0:0:1".equals(host) ? "127.0.0.1" : host), port, url));


        if (jwtToken != null) {
            try {
                executeLogin(request, response);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            String[] anonUrls = anonUrl.split(",");
            // 匹配是否为后端免认证接口
            for (String anonUrl : anonUrls) {
                if (pathMatcher.match(anonUrl, url)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        String token = JWTUtil.getToken(httpServletRequest);
        JWTToken jwtToken = new JWTToken(token);
        try {
            Subject subject = getSubject(request, response);
            subject.login(jwtToken);
            return onLoginSuccess(jwtToken, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(jwtToken, e, request, response);
        }
    }

    //跨域支持
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}