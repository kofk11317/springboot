package com.example.server.token;

import com.example.server.dto.Member;
import com.example.server.testinterface.TestMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtIssueService {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    @Autowired
    private TestMapper testMapper;

    //jwt 토큰 생성 메서드
    public String createJwt(Member member) {
        Date now = new Date();

        Member result = testMapper.findByUserId(member.getId());

        if(result == null) {
            log.info("login failed. id:{}",member.getId());
            return null;
        }
        else {
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                    .setClaims(createClaims(result))
                    .setIssuedAt(now)
                    .setExpiration(new Date(System.currentTimeMillis()+1*(1000*60*60*24*365))) //발급날짜계산
                    .signWith(SignatureAlgorithm.HS256, createSignature())
                    .compact();
        }
    }

    //Claims 생성 메서드. payload 정보 세팅
    private static Map<String, Object> createClaims(Member member) {
        //사용자의 id, password, role을 설정하여 payload에서 정보 조회
        Map<String, Object> claims = new HashMap<>();
        String role = "";
        if("admin".equals(member.getId())){
            role = "ADMIN";
        }
        else{
            role = "USER";
        }
        log.info("userId : {}",member.getId());
        log.info("password : {}",member.getPassword());
        log.info("role : {}",role);

        claims.put("userId", member.getId());
        claims.put("password", member.getPassword());
        claims.put("name", member.getName());
        claims.put("role", role);
        return claims;
    }

    // jwt 서명(Signature) 발급해주는 메서드
    private Key createSignature() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

        public Map<String, Object> validateTokenAndGetClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(createSignature())
                    .parseClaimsJws(token)
                    .getBody();

            // 토큰 만료 검사
            if (claims.getExpiration().before(new Date())) {
                log.error("Token is expired");
                return null;
            }

            // 클레임 정보를 Map으로 변환
            Map<String, Object> claimMap = new HashMap<>(claims);
            log.info("Token validated successfully for user: {}", claimMap.get("userId"));
            return claimMap;

        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return null;
    }
}