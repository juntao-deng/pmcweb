package net.juniper.jmp.monitor.proxy;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.NoRouteToHostException;
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
/**
 * Http Proxy for remote calling, using Java Dynamic proxy will be much better
 * @author juntaod
 *
 */
public class HttpProxy {
	private static final int DEFAULT_TIMEOUT = 40000; //40s
	private Logger logger = Logger.getLogger(HttpProxy.class);
	private static Map<String, HttpProxy> proxyMap = new HashMap<String, HttpProxy>();
	private TargetServerInfo serverInfo;
	private CloseableHttpClient httpclient;
	private HttpProxy(TargetServerInfo info){
		httpclient = HttpClients.createDefault();
		this.serverInfo = info;
	}
	
	public Object request(String serviceName, String method, Object[] params, int timeout) throws Exception {
		String targetUrl = "http://" + serverInfo.getAddress() + ":" + serverInfo.getPort() + "/dispatcher";
		HttpPost httpPost = new HttpPost(targetUrl);
		if(timeout <= -1)
			timeout = DEFAULT_TIMEOUT;
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(timeout);
		builder.setConnectionRequestTimeout(timeout);
		builder.setSocketTimeout(timeout);
		httpPost.setConfig(builder.build());
		
		byte[] bytes = getRequestInfo(serviceName, method, params);
		httpPost.setEntity(new ByteArrayEntity(bytes));
		CloseableHttpResponse resp = null;
		try {
			resp = httpclient.execute(httpPost);
		    HttpEntity entity = resp.getEntity();
		    int length = (int) entity.getContentLength();
		    logger.info("got message for method:" + method + ", length:" + length);
		    if(length <= 0)
		    	return null;
		    
		    ObjectInputStream oin = new ObjectInputStream(entity.getContent());
		    Object result = oin.readObject();
		    EntityUtils.consume(entity);
		    return result;
		}
		catch(NoRouteToHostException e){
			logger.error("NoRouteToHostException for url:" + targetUrl);
		}
		catch(ConnectTimeoutException e){
			logger.error("Connect timeout for url:" + targetUrl);
		}
		catch(HttpHostConnectException e){
			logger.error("HttpHostConnectException for url:" + targetUrl);
		}
		catch(SocketTimeoutException e){
			logger.error("SocketTimeoutException for url:" + targetUrl);
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
