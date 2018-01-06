/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.pw.elka.mkoi.server.crypto;

import java.util.Arrays;
import java.util.BitSet;
//import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
//import org.bouncycastle.jcajce.provider.digest.SHA3;
//import org.bouncycastle.util.encoders.Hex;

/**
 *
 * @author rafal
 */
public class HMAC {
//    public static void main(String[] args) {
//        HMAC hmac=new HMAC();
//        hmac.hmac("key45".getBytes(),"message".getBytes(),new SHA3.Digest512(),576,512);
//    }
//    public void hmac(byte[] key,byte[] message,SHA3.DigestSHA3 SHA3, int blockSize,int outputSize){
//        if(key.length>blockSize)
//            key=SHA3.digest(key);
//        if(key.length < blockSize)
//            key = Arrays.copyOf(key, blockSize);
//        byte[] outer_pad = xorWithConstant((byte)0x5c,key);
//        byte[] inner_pad = xorWithConstant((byte)0x36,key);
//        System.out.println(Hex.toHexString(outer_pad));
//        System.out.println(inner_pad.length+ " " +message.length);
////        return SHA3.digest(outer_pad || SHA3.digest(inner_pad || message));
//    }
//    private byte[] xorWithConstant(byte constant,byte[] key){
//        byte[] out=new byte [key.length];
//        for (int i = 0; i < key.length; i++) {
//            out[i] = (byte) (constant^ key[i]);
//        }
//        return out;
//    }
//    private byte[] xorWithConstant(byte[] constant,byte[] key){
//        byte[] out=new byte [key.length];
//        for (int i = 0; i < key.length; i++) {
////            out[i] = (byte) (constant ^ key[i]);
//        }
//        return out;
//    }
}
