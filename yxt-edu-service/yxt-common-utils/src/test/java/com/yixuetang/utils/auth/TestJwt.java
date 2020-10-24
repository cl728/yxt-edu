package com.yixuetang.utils.auth;

import com.yixuetang.entity.auth.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class TestJwt {

    private static final String pubKeyPath = "E:\\projects\\yxt-edu\\yxt-edu-service\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\projects\\yxt-edu\\yxt-edu-service\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey( pubKeyPath, priKeyPath, "11YXT_USER_AUTH44@" );
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey( pubKeyPath );
        this.privateKey = RsaUtils.getPrivateKey( priKeyPath );
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken( new UserInfo( 20L, "Colin" ), privateKey, 5 );
        System.out.println( "token = " + token );
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiQ29saW4iLCJleHAiOjE2MDM0NTQwNzh9.AXSQyZQS6XwPc06xvxv2LViliu538o8DX3sTNeNGzud_tV9FhVC7s1RDPLZEC4UYcrI_epQSSVbB3lxsZihSMErbPY82_VmWaKhXRZ3vLMuutPrqN_fm2YzHR8zkNxHXEv64ceTFFnmz6bkR409wgxc1jKF1Ultc7dO8-m2JHqE";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken( token, publicKey );
        System.out.println( "id: " + user.getId() );
        System.out.println( "userName: " + user.getUsername() );
    }
}