package com.ortega.vk.chatmanager.client;

import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;

/**
 * This VK client builer is based on  
 * <a href="https://github.com/serezhka/vkdump/blob/master/src/main/java/com/github/serezhka/vkdump/config/VkApiConfig.java">a code sample by Serezhka</a>
 *
 */
public class ProxyClientBuilder implements ClientBuilder {
	private String proxyHost;
	private Integer proxyPort;
	
	public ProxyClientBuilder(String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	@Override
	public VkApiClient buildClient() {
		try {
			TransportClient transportClient = HttpTransportClient.getInstance();
	        Field httpClientField = HttpTransportClient.class.getDeclaredField("httpClient");
	        httpClientField.setAccessible(true);
	        httpClientField.set(null, httpClient());
	
	        return new VkApiClient(transportClient);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public HttpClient httpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContextBuilder contextBuilder = new SSLContextBuilder();
        contextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(contextBuilder.build());

        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setSSLSocketFactory(socketFactory);
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        clientBuilder.setDefaultRequestConfig(requestConfig);

        if (proxyHost != null && proxyPort != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
        }

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(300);
        connectionManager.setDefaultMaxPerRoute(300);
        clientBuilder.setConnectionManager(connectionManager);

        BasicCookieStore cookieStore = new BasicCookieStore();
        clientBuilder.setDefaultCookieStore(cookieStore);
        clientBuilder.setUserAgent("Java VK SDK/0.5.6");

        return clientBuilder.build();
    }
}
