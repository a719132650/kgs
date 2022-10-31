package com.kigooo.kgs.component.kgMvc;
/*
author : Kigooo
verson : 0.0.3
update date : 2022-02-14
*/
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kigooo.kgs.service.kgService.KgRoleService;
import com.kigooo.kgs.util.KgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.kigooo.kgs.component.kgJWT.KgJWTUtil;
import com.kigooo.kgs.component.kgRedis.KgRedisUtil;
import com.kigooo.kgs.component.kgResponseException.KgResponseException;
import com.kigooo.kgs.config.KgProperties;
import com.kigooo.kgs.domain.kgDomain.KgRole;
import com.kigooo.kgs.service.kgService.KgAuthService;

@Component
public class KgMvcIntercept1 implements HandlerInterceptor {

    @Autowired
    private KgRoleService kgRoleService;
    @Autowired
    private KgAuthService kgAuthService;
    @Autowired
    private KgRedisUtil kgRedisUtil;
    @Autowired
    private KgProperties kgProperties;
    @Autowired
    private Environment env;
    private static final Logger logger = LoggerFactory.getLogger("APP_LOG");

    /**
     * 拦截器拦截请求处理 请求前调用
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @return boolean
     * @author
     * @date 2021/8/30 13:10
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String requestUrl = httpServletRequest.getRequestURI().substring(env.getProperty("kgs.apName").length()); // /auth/login
        logger.info("url:"+requestUrl);
        boolean allowVisitFlag = false;

        //无需登陆
        if(kgAuthService.isUnProtectedUrl(requestUrl)){
            allowVisitFlag = true;
        }
        //需登陆 无需验证权限
        else if(kgAuthService.isPublicUrl(requestUrl)){
            String tokenInHead = httpServletRequest.getHeader(kgProperties.getTokenInHeader()); // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAwMDAwMDAwMDEiLCJvaWQiOiIxMDAwMDAwMDAwMDEiLCJpYXQiOjE2MjgxNDEyNzYsImV4cCI6MTYyODE0MzA3Nn0.yT4dANRz-jqCuWHSdj9mXa6BKoKUzq6sfSTmEH_n_0A
            String userOID = KgJWTUtil.getUserOIDByToken(tokenInHead); // 100000000001
            String tokenInRedis = (String)kgRedisUtil.get(kgProperties.getTokenPrefixInRedis() + userOID);
            if(KgUtil.isNotEmpty(tokenInHead) && KgUtil.isNotEmpty(tokenInRedis) && tokenInHead.equals(tokenInRedis)){
                allowVisitFlag = true;
            }
            else{
                throw new KgResponseException(kgProperties.getCode10002(),kgProperties.getMsg10002());
            }
        }
        //需登陆 需验证权限
        else if(kgAuthService.isPrivateUrl(requestUrl)){
            String tokenInHead = httpServletRequest.getHeader(kgProperties.getTokenInHeader()); // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAwMDAwMDAwMDEiLCJvaWQiOiIxMDAwMDAwMDAwMDEiLCJpYXQiOjE2MjgxNDEyNzYsImV4cCI6MTYyODE0MzA3Nn0.yT4dANRz-jqCuWHSdj9mXa6BKoKUzq6sfSTmEH_n_0A
            String userOID = KgJWTUtil.getUserOIDByToken(tokenInHead); // 100000000001
            String tokenInRedis = (String)kgRedisUtil.get(kgProperties.getTokenPrefixInRedis() + userOID);
            if(KgUtil.isNotEmpty(tokenInHead) && KgUtil.isNotEmpty(tokenInRedis) && tokenInHead.equals(tokenInRedis)){
                List<KgRole> urlRoleList = kgRoleService.getRoleListByApi(requestUrl);
                List<KgRole> userRoleList= kgRoleService.getRoleListByUserOID(userOID);
                if(KgUtil.isNotEmpty(userRoleList) && KgUtil.isNotEmpty(urlRoleList)){
                    for(KgRole urlRoleItem : urlRoleList){
                        for(KgRole userRoleItem : userRoleList){
                            if(urlRoleItem.getOid().equals(userRoleItem.getOid())){
                                allowVisitFlag = true;
                                break;
                            }
                        }
                    }
                }
            }
            else{
                throw new KgResponseException(kgProperties.getCode10002(),kgProperties.getMsg10002());
            }
        }
        //未注册的API
        else{
            throw new KgResponseException(kgProperties.getCode10005(),kgProperties.getMsg10005());
        }

        if(allowVisitFlag){
            return true;
        }else{
            throw new KgResponseException(kgProperties.getCode10003(),kgProperties.getMsg10003());
        }
    }

    /**
     * 请求执行完成后调用 未使用
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @return void
     * @author
     * @date 2021/8/30 13:11
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    /**
     * 完全处理请求后调用 未使用
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return void
     * @author
     * @date 2021/8/30 13:11
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
