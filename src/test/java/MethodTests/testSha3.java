/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MethodTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
//import org.bouncycastle.jcajce.provider.digest.SHA3;
//import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author rafal
 */
public class testSha3 {
    
    public testSha3() {
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
//        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
//        byte[] digest = digestSHA3.digest(input.getBytes());
//
//        System.out.println("SHA3-512 = " + Hex.toHexString(digest));
    }
}
