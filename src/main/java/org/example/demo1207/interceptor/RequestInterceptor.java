package org.example.demo1207.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;

import org.example.demo1207.context.UserContext;
import org.example.demo1207.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestInterceptor implements HandlerInterceptor {

    private final User user;
    private final Bucket bucket;

    public RequestInterceptor() {
        this.user = new User();
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(30L)));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        try {

            if (!bucket.tryConsume(1)) {
                response.sendRedirect(
                        "/error?status=" + HttpStatus.TOO_MANY_REQUESTS.value() + "&error=TOO MANY REQUESTS"
                );
            }

            System.out.println("1 - preHandle() : Before sending request to the Controller");
            System.out.println("Method Type: " + request.getMethod());
            System.out.println("Request URL: " + request.getRequestURI());

            user.setFirstname(StringUtils.trimToNull(request.getParameter("first-name")));
            user.setLastname(StringUtils.trimToNull(request.getParameter("last-name")));
            user.setEmail(StringUtils.trimToNull(request.getParameter("email")));
            UserContext.removeCurrentUser();
            UserContext.setCurrentUser(user);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        try {
            System.out.println("2 - postHandle() : After the Controller serves the request (before returning back response to the client)");
            Map<String, Object> model = modelAndView.getModel();
            if (model.get("totalPages") != null && (int) (model.get("totalPages")) == 0) {
                modelAndView.addObject("interceptorMessage", "No data available for your request");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {
        try {
            System.out.println("3 - afterCompletion() : After the request and Response is completed");
            System.out.println("-------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
