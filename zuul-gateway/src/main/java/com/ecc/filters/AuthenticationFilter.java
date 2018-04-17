package com.ecc.filters;

import com.ecc.web.api.UserServiceApi;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFilter extends ZuulFilter {
    @Autowired
    UserServiceApi userServiceApi;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("peer accessing gateway");
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();

        if (!(requestURI.contains("register") || requestURI.contains("login") || requestURI.contains("verify"))) {
            String accessToken = request.getParameter("token");
            if (accessToken == null || accessToken.equals("")) {
                context.setSendZuulResponse(false);
                context.setResponseBody("Access denied, no access token found in request params.");
                context.setResponseStatusCode(400);
                return null;
            } else {
                System.out.println("authentication in processing!");
                if (userServiceApi.checkIfTokenExists(accessToken)) {
                    context.setSendZuulResponse(false);
                    context.setResponseBody("Access denied, token error.");
                    context.setResponseStatusCode(400);
                }
            }
        }

        context.setSendZuulResponse(true);
        return null;
    }

}
