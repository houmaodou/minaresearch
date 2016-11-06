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
	        //����Connector������  
	        NioSocketConnector connector = new NioSocketConnector();  
	        //�������ӳ�ʱʱ��  
	        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);  
	          
	        connector.getFilterChain().addLast("codec",  
	                new ProtocolCodecFilter(new TextLineCodecFactory()));  
	        connector.getFilterChain().addLast("logger", new LoggingFilter());  
	        //�ǳ��򵥵Ĵ���Handler�������������һ������  
	        connector.setHandler(new ClientSessionHandler(1));  
	        IoSession session = null;  
	  
	        try {  
	            //����Զ������������IP�Ͷ˿�  
	            ConnectFuture future = connector.connect(new InetSocketAddress(  
	                    HOSTNAME, PORT));  
	            //�ȴ����ӽ���  
	            future.awaitUninterruptibly();  
	            //���ӽ����󷵻ػỰsession  
	            session = future.getSession();  
	        } catch (RuntimeIoException e) {  
	            System.err.println("Failed to connect.");  
	            e.printStackTrace();  
	            Thread.sleep(5000);  
	        } finally{  
	            if(session!=null){  
	                //�ȴ���������ͨ�������������ж�ʽ�������ȴ�  
	                session.getCloseFuture().awaitUninterruptibly();  
	            }  
	        }  
	        //�ر�����  
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
