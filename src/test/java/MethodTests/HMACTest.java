/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodTests;

import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import pl.edu.pw.elka.mkoi.server.crypto.HMAC;

/**
 *
 * @author rafal
 */
public class HMACTest {
    
    public HMACTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testSha3() throws Exception {
        String input = "Hello world !";
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(input.getBytes());
        assertEquals("e9a4fd120d684537d57f314d51213ce5acef8ff6974e2b7599674ef0f0a3cf111f0d26ed602db946739da448210fb76a7621d7a8f07728372b10a975f36d8e37",
                Hex.toHexString(digest));
    }
    @Test
    public void hmacSHA256Test() throws Exception {
        HMAC hmac = new HMAC();
        byte[] out = 
                hmac.hmac("key".getBytes(), "The quick brown fox jumps over the lazy dog".getBytes(), new SHA256.Digest(), 64);
        assertEquals("f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8", Hex.toHexString(out));
    }
    @Test
    public void hmacMD5Test() throws Exception {
        HMAC hmac = new HMAC();
        byte[] out = 
                hmac.hmac("key".getBytes(), "The quick brown fox jumps over the lazy dog".getBytes(), new MD5.Digest(), 64);
        assertEquals("80070713463e7749b90c2dc24911e275", Hex.toHexString(out));
    }
}
