package com.yesido.lib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * HttpClient请求工具
 * 
 * @author yesido
 * @date 2019年8月8日 下午5:15:26
 */
public class HttpClient extends HttpEntityEnclosingRequestBase {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    public final static String POST = "POST";
    public final static String GET = "GET";
    public final static String DELETE = "DELETE";
    public final static String PUT = "PUT";

    private String method;
    private String url;
    private Map<String, Object> param;

    private int contentType;
    public final static int APPLICATION_DEFAULT = 0; // 默认from表单提交
    public final static int APPLICATION_FORM_URLENCODED = 0; // from表单提交
    public final static int APPLICATION_JSON = 1; // json提交
    private boolean keepAlive = false;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public HttpClient putHeader(String key, String value) {
        this.addHeader(key, value);
        return this;
    }

    public HttpClient addParam(String key, Object value) {
        if (param == null) {
            param = new HashMap<>();
        }
        param.put(key, value);
        return this;
    }


    private HttpClient() {
        super();
    }

    private HttpClient(String method, String url, int contentType, boolean keepAlive) {
        super();
        this.method = method;
        this.url = url;
        this.contentType = contentType;
        this.keepAlive = keepAlive;
    }

    /**
     * 开启https
     * 
     * @return
     */
    private static CloseableHttpClient createHttpsClient() {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        //指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    // 信任任何链接
                    return true;
                }
            };
            //SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            //LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore, anyTrustStrategy).build();
            SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            registryBuilder.register("https", sslsf);

        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        //设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        //构建客户端
        return HttpClientBuilder.create().setConnectionManager(connManager).build();
    }


    public static HttpClient newPost(String url) {
        return newInstance(POST, url, null, APPLICATION_DEFAULT, false);
    }

    public static HttpClient newPost(String url, Map<String, Object> param) {
        return newInstance(POST, url, param, APPLICATION_DEFAULT, false);
    }

    public static HttpClient newGet(String url) {
        return newInstance(GET, url, null, APPLICATION_DEFAULT, false);
    }

    public static HttpClient newGet(String url, Map<String, Object> param) {
        return newInstance(GET, url, param, APPLICATION_DEFAULT, false);
    }

    private static HttpClient newInstance(String method, String url, Map<String, Object> param,
            int contentType, boolean keepAlive) {
        HttpClient request = new HttpClient(method, url, contentType, keepAlive);
        request.setParam(param);
        return request;
    }

    private static boolean isHttps(String url) {
        if (url.startsWith("https")) {
            return true;
        }
        return false;
    }

    /**
     * 执行并返回结果
     * 
     * @return String
     */
    public String getString() {
        try {
            HttpEntity entity = execute();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (UnsupportedOperationException | IOException e) {
            LOGGER.error("请求异常：{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 执行并返回结果
     * 
     * @return InputStream
     */
    public InputStream getInputStream() {
        try {
            HttpEntity entity = execute();
            if (entity != null) {
                return entity.getContent();
            }
        } catch (UnsupportedOperationException | IOException e) {
            LOGGER.error("请求异常：{}", e.getMessage(), e);
        }
        return null;
    }

    private HttpEntity execute() throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient;
        if (isHttps(url)) {
            httpClient = createHttpsClient();
        } else {
            httpClient = HttpClients.createDefault();
        }
        if (param != null && !param.isEmpty()) {
            handlerParam();
        }
        this.setURI(URI.create(url));
        this.setHeader("Connection", keepAlive ? "Keep-alive" : "close");
        return httpClient.execute(this).getEntity();
    }

    /**
     * 处理请求参数
     * 
     * @throws UnsupportedEncodingException
     */
    private void handlerParam() throws UnsupportedEncodingException {
        if ("post".equalsIgnoreCase(method) || "put".equalsIgnoreCase(method)) {
            if (this.contentType == APPLICATION_DEFAULT) {
                // application/x-www-form-urlencoded
                List<NameValuePair> qparams = new ArrayList<>();
                for (Entry<String, Object> entry : param.entrySet()) {
                    qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(qparams, "UTF-8");
                this.setEntity(entity);
            } else if (this.contentType == APPLICATION_JSON) {
                // application/json
                this.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
                StringEntity entity = new StringEntity(JSONObject.toJSONString(param), "UTF-8");
                entity.setContentType("text/json;charset=UTF-8");
                entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
                this.setEntity(entity);
            }
        } else if ("get".equalsIgnoreCase(method) || "delete".equalsIgnoreCase(method)) {
            for (Entry<String, Object> entry : param.entrySet()) {
                if (this.url.indexOf("?") != -1) {
                    this.url += "&" + entry.getKey() + "=" + entry.getValue();
                } else {
                    this.url += "?" + entry.getKey() + "=" + entry.getValue();
                }
            }
        }

    }
}
