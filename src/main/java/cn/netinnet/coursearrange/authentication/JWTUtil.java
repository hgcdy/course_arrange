package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.entity.UserInfo;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.authc.ExpiredCredentialsException;

import java.util.Date;

public class JWTUtil {

    private static final String SECRET_KEY = "secret";

    /**
     * 生成token
     * @param userInfo
     * @return
     */
    public static String getToken(UserInfo userInfo) {
        Date date = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("userInfo", JSON.toJSONString(userInfo))
                .withExpiresAt(date)
                .sign(algorithm);
    }

    /**
     * 从token获取用户信息
     * @param token
     * @return
     */
    public static UserInfo getUserInfo(String token) {
        DecodedJWT jwt = JWT.decode(token);
        UserInfo userInfo = JSON.parseObject(jwt.getClaim("userInfo").asString(), UserInfo.class);
        return userInfo;
    }


    /**
     * token校验
     * @param token
     * @return
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new ExpiredCredentialsException(e.getMessage());
        } catch (Exception e) {
            System.out.println("token校验失败");
            return false;
        }
    }
}
