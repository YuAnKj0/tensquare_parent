package com.tensquare.encrypt.filter;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.tensquare.encrypt.rsa.RsaKeys;
import com.tensquare.encrypt.service.RsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Ykj
 * @date 2022/11/6/15:05
 * @apiNote
 */
public class RSARequestFilter extends ZuulFilter {
    
    @Autowired
    private RsaService rsaService;
    
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
    
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER+1;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();
        try {
            String decryptData=null;
            
            
            String url = request.getRequestURL().toString();
            InputStream inputStream = currentContext.getRequest().getInputStream();
            String requestParam = StreamUtils.copyToString(inputStream, Charsets.UTF_8);
            if (!Strings.isNullOrEmpty(requestParam)) {
                System.out.println(String.format("请求体中的密文: %s",requestParam));
                decryptData = rsaService.RSADecryptDataPEM(requestParam, RsaKeys.getServerPrvKeyPkcs8());
                System.out.println(String.format("解密后的内容: %s", decryptData));
                
            }
            System.out.println(String.format("request: %s >>> %s, data=%s",
                    request.getMethod(),url,decryptData));
    
            if (!Strings.isNullOrEmpty(decryptData)) {
                System.out.println("json字符串写入request body");
                final byte[] reqBodyBytes = decryptData.getBytes();
                currentContext.setRequest(new HttpServletRequestWrapper(request){
    
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }
                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }
    
                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
                
            }
            System.out.println("转发request");
            currentContext.addZuulRequestHeader("Content-Type", 
                    String.valueOf(MediaType.APPLICATION_JSON) + ";charset=UTF-8");
    
        }catch (Exception e){
            System.out.println(this.getClass().getName() + "运行出错" + e.getMessage());
        }
        return null;
    }
}
