package com.example.aihub.aspect;

import java.lang.reflect.Field;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.aihub.annotation.CheckDataOwner;
import com.example.aihub.exception.PermissionDeniedException;
import com.example.aihub.service.ResourceService;

import cn.dev33.satoken.stp.StpUtil;

@Aspect
@Component
public class DataOwnerAspect {
    @Autowired
    private ApplicationContext context;

    @Around("@annotation(checkDataOwner)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, CheckDataOwner checkDataOwner) throws Throwable {
        Integer targetId = extractIdFromArgs(
                joinPoint.getArgs(),
                checkDataOwner.index(),
                checkDataOwner.idField()
        );

        ResourceService service = (ResourceService) context.getBean(checkDataOwner.serviceClass());
        Integer ownerId = service.getOwnerIdById(targetId);
        Integer userId = StpUtil.getLoginIdAsInt();

        if (ownerId != null && !userId.equals(ownerId) && !StpUtil.hasRole("admin")) {
            throw new PermissionDeniedException("You cannot access this resource!");
        }

        return joinPoint.proceed();
    }

    private Integer extractIdFromArgs(Object[] args, int index, String idField) {
        Object paramValue = args[index];
        if (paramValue instanceof Integer) {
            return (Integer) paramValue;
        }

        try {
            Field field = paramValue.getClass().getDeclaredField(idField);
            field.setAccessible(true);
            return (Integer) field.get(paramValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can not extract id from args!", e);
        }
    }
}
