package vn.nhom.thuvien.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class TienIchJwt {

    private static final String LOAI_ACCESS = "access";
    private static final String LOAI_REFRESH = "refresh";

    private final SecretKey khoa;
    private final Duration hanAccess;
    private final Duration hanRefresh;

    public TienIchJwt(@Value("${thuvien.jwt.secret}") String biMat,
                      @Value("${thuvien.jwt.access-phut}") long accessPhut,
                      @Value("${thuvien.jwt.refresh-ngay}") long refreshNgay) {
        byte[] bytes = biMat.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET phải dài tối thiểu 32 ký tự");
        }
        this.khoa = Keys.hmacShaKeyFor(bytes);
        this.hanAccess = Duration.ofMinutes(accessPhut);
        this.hanRefresh = Duration.ofDays(refreshNgay);
    }

    public String taoAccessToken(NguoiDungDangNhap nd) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(String.valueOf(nd.nguoiDungId()))
                .claim("loai", LOAI_ACCESS)
                .claim("ten", nd.tenDangNhap())
                .claim("vaiTro", nd.vaiTro())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(hanAccess)));
        if (nd.banDocId() != null) {
            builder.claim("banDocId", nd.banDocId());
        }
        return builder.signWith(khoa).compact();
    }

    // Refresh token chỉ chứa mã người dùng và một mã định danh riêng (jti) để thu hồi khi đăng xuất
    public String taoRefreshToken(Long nguoiDungId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(nguoiDungId))
                .id(UUID.randomUUID().toString())
                .claim("loai", LOAI_REFRESH)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(hanRefresh)))
                .signWith(khoa)
                .compact();
    }

    public long soGiayRefresh() {
        return hanRefresh.toSeconds();
    }

    // Trả về null nếu token sai chữ ký, hết hạn hoặc không phải access token
    public NguoiDungDangNhap docAccessToken(String token) {
        Claims c = docClaims(token);
        if (c == null || !LOAI_ACCESS.equals(c.get("loai", String.class))) {
            return null;
        }
        Number banDocId = c.get("banDocId", Number.class);
        return new NguoiDungDangNhap(Long.valueOf(c.getSubject()), c.get("ten", String.class),
                c.get("vaiTro", String.class), banDocId == null ? null : banDocId.longValue());
    }

    public Claims docRefreshToken(String token) {
        Claims c = docClaims(token);
        if (c == null || !LOAI_REFRESH.equals(c.get("loai", String.class))) {
            return null;
        }
        return c;
    }

    private Claims docClaims(String token) {
        try {
            return Jwts.parser().verifyWith(khoa).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
