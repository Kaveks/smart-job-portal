
package com.kaveks.commonweb.security.authorization;

import com.kaveks.commonweb.security.annotation.RequireRoles;
import com.kaveks.commonlib.domain.UserRole;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public class RoleAuthorizationManager
        implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authenticationSupplier,
            MethodInvocation invocation
    ) {

        Authentication authentication = authenticationSupplier.get();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        assert invocation != null;
        RequireRoles requireRoles = findRequireRoles(invocation);

        // Deny access if no role annotation is found.
        if (requireRoles == null || requireRoles.value().length == 0) {
            return new AuthorizationDecision(false);
        }

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        boolean hasRequiredRole = Arrays.stream(requireRoles.value())
                .map(UserRole::name)
                .anyMatch(requiredRole ->
                        authorities.stream().anyMatch(authority ->
                                requiredRole.equals(authority.getAuthority())
                        )
                );

        return new AuthorizationDecision(hasRequiredRole);
    }

    private RequireRoles findRequireRoles(MethodInvocation invocation) {

        Object aThis = invocation.getThis();
        assert aThis != null;
        Class<?> targetClass = AopUtils.getTargetClass(aThis);

        Method specificMethod = AopUtils.getMostSpecificMethod(
                invocation.getMethod(),
                targetClass
        );

        // Method-level annotation takes precedence.
        RequireRoles methodAnnotation =
                AnnotatedElementUtils.findMergedAnnotation(
                        specificMethod,
                        RequireRoles.class
                );

        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // Fall back to the class-level annotation.
        return AnnotatedElementUtils.findMergedAnnotation(
                targetClass,
                RequireRoles.class
        );
    }
}
