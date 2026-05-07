package com.re.config;

import com.re.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        String uri = request.getRequestURI();

        if (user == null) {
            response.sendRedirect("/auth/login");
            return false;
        }


        String role = user.getRole().name();


        boolean isAccessingLecturer = uri.startsWith("/lecturer");
        boolean isAccessingAdmin = uri.startsWith("/admin");
        boolean isAccessingStudent = uri.startsWith("/student");

        // Nếu truy cập sai vùng quyền
        if ((isAccessingLecturer && !role.equals("LECTURER")) ||
                (isAccessingAdmin && !role.equals("ADMIN")) ||
                (isAccessingStudent && !role.equals("STUDENT"))) {

            // Bật về trang tương ứng với Role của họ
            response.sendRedirect(getRedirectUrlByRole(role));
            return false;
        }

        return true;
    }

    // Hàm phụ trợ để xác định URL trang chủ theo Role
    private String getRedirectUrlByRole(String role) {
        switch (role) {
            case "ADMIN":
                return "/admin/dashboard";
            case "LECTURER":
                return "/lecturer/dashboard";
            case "STUDENT":
                return "/student/dashboard";
            default:
                return "/auth/login";
        }
    }
}