package cn.netinnet.coursearrange.authentication;

import cn.netinnet.coursearrange.entity.UserInfo;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JWTUtil {

    private static final String SECRET_KEY = "secret";

    /**
     * 生成token
     * @param userInfo
     * @return
     */
    public static String sign(UserInfo userInfo) {
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
            System.out.println("token校验成功");
            return true;
        } catch (TokenExpiredException e) {
            System.out.println("token已过期");
            //return false;
            throw new ExpiredCredentialsException(e.getMessage());
        } catch (Exception e) {
            System.out.println("token校验失败");
            return false;
        }
    }

    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        //增加token从url中获取途径
        if (token == null) {
            token = request.getParameter("token");
        }
        if (StringUtils.isBlank(token)) {
            token = null;
        }
        return token;
    }
}
