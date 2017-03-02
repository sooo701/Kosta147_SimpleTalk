package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JOptionPane;

import dbConn.util.ConnectionHelper;
import server.Server;

public class Server_Controller extends Server { // 서버 컨트롤러

	// Net Source
	private ServerSocket serverSocket = null;
	private Socket socket;
	final int port = 7777;
	PreparedStatement pstmt = null;
	String sql = null;
	ResultSet rs = null;
	String temp = "";

	public Server_Controller() { // 생성자 함수
		start();
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트", "알림", JOptionPane.ERROR_MESSAGE);
		}
		if (serverSocket != null) {
			System.out.println("연결성공");
			connect();
		}
	}// start

	public class Socket_Thread implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					
					textArea.append("사용자 접속 대기중\n");
					socket = serverSocket.accept();
					
					Connection conn = ConnectionHelper.getConnection("oracle");
					
					sql = "select * from userlist where ip = ?";

					
					temp = socket.getInetAddress().toString();
					
					System.out.println("접속 ip = " + temp);
					String currentIp = "";
					for (int i = 1; i < temp.length(); i++) {
						currentIp += String.valueOf(temp.charAt(i));
					}

					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, currentIp);

					rs = pstmt.executeQuery();
					
					System.out.println(currentIp);
				
					
					if (rs.next()) {
						System.out.println(rs.getString(1));
						System.out.println(rs.getString(2));
						System.out.println(rs.getString(3));
						String nickName = rs.getString(4);
						textArea.append(nickName + "님이 입장s하셨습니다.\n");
					}
					
					System.out.println("if 통과지점");

				} catch (Exception e) {
					textArea.append("서버가 중지 되었습니다\n");
					break;
				} // try-catch
			} // while
		}// run

	} // 스레드

	private void connect() {

		Thread thread = new Thread(new Socket_Thread());
		thread.start();

	}

}// class end
