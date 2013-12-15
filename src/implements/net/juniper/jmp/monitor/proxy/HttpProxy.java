package net.juniper.jmp.monitor.proxy;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import net.juniper.jmp.monitor.core.InvocationInfo;
import net.juniper.jmp.monitor.mo.info.TargetServerInfo;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpProxy {
	private Logger logger = Logger.getLogger(HttpProxy.class);
	private static Map<String, HttpProxy> proxyMap = new HashMap<String, HttpProxy>();
	private TargetServerInfo serverInfo;
	private CloseableHttpClient httpclient;
	private HttpProxy(TargetServerInfo info){
		httpclient = HttpClients.createDefault();
		this.serverInfo = info;
	}
	
//	public byte[] request(String serviceName, String method, Object[] params) throws Exception {
//		return doRequest(serviceName, method, params, -1);
//	}
	
	public Object request(String serviceName, String method, Object[] params, int timeout) throws Exception {
		String targetUrl = "http://" + serverInfo.getAddress() + ":" + serverInfo.getPort() + "/dispatcher";
		HttpPost httpPost = new HttpPost(targetUrl);
		if(timeout <= -1)
			timeout = 20000;
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(timeout);
		builder.setConnectTimeout(timeout);
		builder.setSocketTimeout(timeout);
		httpPost.setConfig(builder.build());
		
		byte[] bytes = getRequestInfo(serviceName, method, params);
		httpPost.setEntity(new ByteArrayEntity(bytes));
		CloseableHttpResponse resp = null;
		try {
			resp = httpclient.execute(httpPost);
//		    System.out.println(resp.getStatusLine());
		    HttpEntity entity = resp.getEntity();
		    int length = (int) entity.getContentLength();
		    if(length <= 0)
		    	return null;
		    
		    ObjectInputStream oin = new ObjectInputStream(entity.getContent());
		    Object result = oin.readObject();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity);
		    return result;
		}
		catch(ConnectTimeoutException e){
			logger.error("Connect timeout for url:" + targetUrl);
		}
		catch(HttpHostConnectException e){
			logger.error("Connect timeout for url:" + targetUrl);
		}
		catch(SocketTimeoutException e){
			logger.error("Connect timeout for url:" + targetUrl);
		}
		catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		finally {
			if(resp != null)
				resp.close();
		}
		return null;
	}
	
	private byte[] getRequestInfo(String serviceName, String method, Object[] params) throws Exception{
		InvocationInfo info = new InvocationInfo();
		info.setClassName(serviceName);
		info.setMethod(method);
		info.setParams(params);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bout);
		output.writeObject(info);
		return bout.toByteArray();
	}

	public static HttpProxy getInstance(TargetServerInfo info) {
		String key = info.getAddress() + "_" + info.getPort();
		HttpProxy proxy = proxyMap.get(key);
		if(proxy == null){
			proxy = new HttpProxy(info);
			proxyMap.put(key, proxy);
		}
		return proxy;
	}
}
