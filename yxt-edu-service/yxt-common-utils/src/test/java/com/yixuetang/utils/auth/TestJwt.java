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
        String token = JwtUtils.generateToken( new UserInfo( 20L, "Colin", false, "http://www.pava.run/group1/M00/00/00/rBAABV9oD7yAZtXEAAAnvyJPq-0710.jpg", true ), privateKey, 5 );
        System.out.println( "token = " + token );
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiQ29saW4iLCJyZWFsTmFtZSI6IuW6hOeBv-aelyIsImF2YXRhciI6Imh0dHA6Ly93d3cucGF2YS5ydW4vZ3JvdXAxL00wMC8wMC8wMC9yQkFBQlY5b0Q3eUFadFhFQUFBbnZ5SlBxLTA3MTAuanBnIiwicmVtZW1iZXJNZSI6dHJ1ZSwiZXhwIjoxNjAzNjQxNTc4fQ.KOB3W1bevy6VKddBc2CNVlOoYqpNbKCN2KDqDzfFbDGYsdCgOgkJOAfEoa2T1P_KXS7EtVdrYYT9qWUwfLgUXMDTSQhRhj1jhYcSSDHehn447oYmtKvL8hIEyuRAX3zIjXzP_2jDoYMlvgmVqLEj0x-10kHTdbJ5PV5h1dkfVQU";

        // 解析token
        System.out.println( JwtUtils.getInfoFromToken( token, publicKey ) );

    }
}