package cn.yourdad.aop;

import cn.yourdad.pojo.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * @program: MusicRecommendationSystem
 * @package: cn.yourdad.aop
 * @description: 记录请求
 * @author: wzj
 * @create: 2020-10-15 22:17
 **/

@Aspect
@Component
public class RequestLogAspect {

    private static final Logger logger = LoggerFactory.getLogger("RequestLog");
    private static final String pointcut = "execution(* cn.yourdad.controller..*.*(..))";

    @Pointcut(value = pointcut)
    public void requestLog(){}


    @Before("requestLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //ip, url
        logger.info("requestlogs \tip:{}\tmethod:{}\tclass_method:{}\targs:{}\turl:{}", request.getRemoteAddr(),
                request.getMethod(), joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                Arrays.asList(joinPoint.getArgs()), request.getRequestURL());
    }

    @AfterReturning(returning = "object", pointcut = "requestLog()")
    public void doAfterReturning(Object object) {
        logger.info("response:{}", object.toString());
    }
}
