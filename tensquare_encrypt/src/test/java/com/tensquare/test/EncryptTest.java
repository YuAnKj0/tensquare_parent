package com.tensquare.test;

import com.tensquare.encrypt.EncryptApplcation;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Ykj
 * @date 2022/11/6/14:59
 * @apiNote
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EncryptApplcation.class)
public class EncryptTest {
    
    @Autowired
    private RsaService rsaService;
    
    @Before
    public void before()throws Exception{}
    @After
    public void after()throws Exception{}
    @Test
    public void genEncryptDataByPubKey(){
        //此处可替换为你自己的请求参数json字符串
        String data = "{\"labelname\":\"java\"}";
        String encData = null;
        try {
            encData = rsaService.RSAEncryptDataPEM(data, RsaKeys.getServerPubKey());
            System.out.println("data: " + data);
            System.out.println("encData: " + encData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
}
