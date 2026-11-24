package vn.nhom.thuvien.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Đọc access token trong header Authorization và xác định người gọi là ai, vai trò gì
@Component
public class BoLocJwt extends OncePerRequestFilter {

    private final TienIchJwt tienIchJwt;

    public BoLocJwt(TienIchJwt tienIchJwt) {
        this.tienIchJwt = tienIchJwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            NguoiDungDangNhap nd = tienIchJwt.docAccessToken(header.substring(7));
            if (nd != null) {
                var auth = new UsernamePasswordAuthenticationToken(nd, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + nd.vaiTro())));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
