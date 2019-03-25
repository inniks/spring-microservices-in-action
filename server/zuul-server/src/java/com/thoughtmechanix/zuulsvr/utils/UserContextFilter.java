package com.thoughtmechanix.zuulsvr.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * The filter class to get some contextual information from the HTTP header of 
 * the REST call.
 * 
 * <p>This solution is the way to propagate the parameters in the HTTP header 
 * of the REST call to any downstream service calls.
 * 
 * <p>The basic logic is the {@code UserContextFilter} will get several 
 * parameters from the incoming HTTP header and store them into the 
 * {@code UserContext} object of the {@code UserContextHolder}. 
 * The {@code UserContextInterceptor} will inject the parameters from the 
 * {@code UserContextHolder} into the header of the outgoing service requests 
 * toward downstream services.
 * 
 * <p>This class will get following parameters from the HTTP header of the 
 * incoming REST call:
 * <ul>
 *   <li>CORRELATION_ID (tmx-correlation-id)
 *   <li>AUTH_TOKEN     (tmx-auth-token)
 *   <li>USER_ID        (tmx-user-id)
 *   <li>ORG_ID         (tmx-org-id)
 * </ul>
 *
 * @author  Wuyi Chen
 * @date    03/25/2019
 * @version 1.0
 * @since   1.0
 */
@Component
public class UserContextFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext().setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));  // This filter class retrieves the correlation ID from the header and sets the value on the UserContext class.
        UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

        logger.debug("UserContextFilter Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
