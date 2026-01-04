package com.example.common.interceptor;//package com.example.backend.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Component
//public class DemoInterceptor implements HandlerInterceptor {
//
//    private static final Logger log = LoggerFactory.getLogger(DemoInterceptor.class);
//    // 在请求处理之前进行调用
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("preHandle");
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//    }
//    // 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("postHandle");
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }
//    // 在请求处理完成之后，渲染视图之后进行调用
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//    }
//}
