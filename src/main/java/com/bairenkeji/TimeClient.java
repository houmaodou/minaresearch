package com.bairenkeji;

import java.net.InetSocketAddress;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class TimeClient {

	  private static final String HOSTNAME = "127.0.0.1";  
	    private static final int PORT = 9123;  
	    private static final long CONNECT_TIMEOUT = 30 * 1000L; // 30 seconds  
	  
	    public static void main(String[] args) throws Throwable {  
	        //创建Connector连接器  
	        NioSocketConnector connector = new NioSocketConnector();  
	        //设置连接超时时间  
	        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);  
	          
	        connector.getFilterChain().addLast("codec",  
	                new ProtocolCodecFilter(new TextLineCodecFactory()));  
	        connector.getFilterChain().addLast("logger", new LoggingFilter());  
	        //非常简单的处理Handler，向服务器发送一个数字  
	        connector.setHandler(new ClientSessionHandler(1));  
	        IoSession session = null;  
	  
	        try {  
	            //连接远程主机，设置IP和端口  
	            ConnectFuture future = connector.connect(new InetSocketAddress(  
	                    HOSTNAME, PORT));  
	            //等待连接建立  
	            future.awaitUninterruptibly();  
	            //连接建立后返回会话session  
	            session = future.getSession();  
	        } catch (RuntimeIoException e) {  
	            System.err.println("Failed to connect.");  
	            e.printStackTrace();  
	            Thread.sleep(5000);  
	        } finally{  
	            if(session!=null){  
	                //等待本次连接通话结束，不可中断式的阻塞等待  
	                session.getCloseFuture().awaitUninterruptibly();  
	            }  
	        }  
	        //关闭连接  
	        connector.dispose();  
	    }  
	    static class ClientSessionHandler extends IoHandlerAdapter {  
	        private final int value;  
	        public ClientSessionHandler(int value) {  
	            this.value = value;  
	        }  
	        @Override  
	        public void sessionOpened(IoSession session) {  
	            session.write(value);  
	        }  
	  
	        @Override  
	        public void messageReceived(IoSession session, Object message) {  
	            System.out.println(message);  
	            session.close(true);  
	        }  
	  
	        @Override  
	        public void exceptionCaught(IoSession session, Throwable cause) {  
	            session.close(true);  
	        }  
	    }  
}
