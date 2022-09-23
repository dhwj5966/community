package com.starry.community.configuration;

import com.starry.community.util.CommunityConstant;
import com.starry.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Starry
 * @create 2022-09-23-4:44 PM
 * @Describe SpringSecurity权限控制配置类
 */
@Configuration
public class Security_conf extends WebSecurityConfigurerAdapter implements CommunityConstant {

    /**
     * 该方法负责配置忽略权限检查的类,
     * 不检查所有的静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }


    /*
        登录认证的模块已经开发完毕，不再进行重写，只重写授权的逻辑
        认证是什么？认证是赋予请求权限的逻辑，如验证用户名密码，扫码，验证验证码等很多种方式都可以作为认证的方式。
        授权是什么？设置哪些路径需要哪些权限才可以访问，以及越权访问的处理逻辑。
     */

    /**
     * 该方法负责授权控制，授权控制的含义是：1.对需要授权的路径进行详细配置。2，设置越权操作的处理逻辑
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权逻辑
        http.authorizeRequests()
                .antMatchers(
                        //那些需要有登录权限的路径
                        //拥有这三类权限的请求，才可以访问以上路径
                        "/comment/add/*"
                ).hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                //其他所有的请求，都可以被任何权限的请求访问。
                .anyRequest().permitAll();

        //当请求越权时的处理逻辑
        http.exceptionHandling()
                //未登录时，如何处理？
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request,
                                         HttpServletResponse response, AuthenticationException authException)
                            throws IOException, ServletException {
                        //需要进行判断，该请求是一个同步请求还是一个异步请求
                        String xRequest = request.getHeader("x-requested-with");
                        if (!"XMLHttpRequest".equals(xRequest)) {
                            //如果是同步请求，重定向到登陆页面即可
                            response.sendRedirect(request.getContextPath() + "/login");
                        } else {
                            //如果是异步请求，返回JSON数据
                            //首先需要设置ContentType
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "您还未登录！"));
                        }
                    }
                })
                //登录了，但访问了权限不足的路径，如何处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request,
                                       HttpServletResponse response, AccessDeniedException accessDeniedException)
                            throws IOException, ServletException {
                        String xRequest = request.getHeader("x-requested-with");
                        if (!"XMLHttpRequest".equals(xRequest)) {
                            //如果是同步请求，重定向到登陆页面即可
                            response.sendRedirect(request.getContextPath() + "/error");
                        } else {
                            //如果是异步请求，返回JSON数据
                            //首先需要设置ContentType
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "您没有访问此功能的权限！"));
                        }
                    }
                });


        //Spring Security 会自动过滤/logout路径的请求，进行退出，但是项目需要用之前写的Controller处理/logout请求
        //需要覆盖掉它的逻辑,把该filter关注的路径改掉
        http.logout().logoutUrl("/asfacgjvh4ga");
    }
}
