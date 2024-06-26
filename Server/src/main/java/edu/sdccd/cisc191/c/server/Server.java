package edu.sdccd.cisc191.c.server;

import edu.sdccd.cisc191.c.CustomerRequest;
import edu.sdccd.cisc191.c.CustomerResponse;

import java.net.*;
import java.io.*;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example).  Note that this server processes each connection
 * as it is received, rather than creating a separate thread
 * to process the connection.
 */
//public class Server {
//    private ServerSocket serverSocket;
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//
//    public void start(int port) throws Exception {
//        serverSocket = new ServerSocket(port);
//        clientSocket = serverSocket.accept();
//        out = new PrintWriter(clientSocket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            CustomerRequest request = CustomerRequest.fromJSON(inputLine);
//            CustomerResponse response = new CustomerResponse(request.getId(), "Jane", "Doe");
//            out.println(CustomerResponse.toJSON(response));
//        }
//    }
//
//    public void stop() throws IOException {
//        in.close();
//        out.close();
//        clientSocket.close();
//        serverSocket.close();
//    }
//
//    public static void main(String[] args) {
//        Server server = new Server();
//        try {
//            server.start(4444);
//            server.stop();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//} //end class Server

import java.util.*;

public class Server {
    private ServerSocket serverSocket;

    // Predefined customer details
    private static final List<CustomerDetails> customerDetailsList = Arrays.asList(
            new CustomerDetails(1, "John", "Smith"),
            new CustomerDetails(2, "Jane", "Doe"),
            new CustomerDetails(3, "Michael", "Johnson"),
            new CustomerDetails(4, "Emily", "Brown"),
            new CustomerDetails(5, "David", "Wilson")
    );

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket);

            // Handle client in a new thread
            new ClientHandler(clientSocket).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(4444);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    CustomerRequest request = CustomerRequest.fromJSON(inputLine);
                    System.out.println("Request received from client: " + request.toString());

                    // Get next customer details from the list cyclically
                    CustomerDetails customerDetails = getNextCustomerDetails();

                    // Create response with the selected customer details
                    CustomerResponse response = new CustomerResponse(
                            customerDetails.getId(),
                            customerDetails.getFirstName(),
                            customerDetails.getLastName()
                    );

                    out.println(CustomerResponse.toJSON(response));
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private CustomerDetails getNextCustomerDetails() {
            // Get next customer details from the list cyclically
            int index = getNextIndex();
            return customerDetailsList.get(index);
        }

        private synchronized int getNextIndex() {
            // Calculate the next index in a thread-safe manner
            if (index >= customerDetailsList.size()) {
                index = 0; // Reset index if exceeds list size
            }
            return index++;
        }

        // Maintain index to cycle through customer details
        private int index = 0;
    }

    // Customer details class
    private static class CustomerDetails {
        private int id;
        private String firstName;
        private String lastName;

        public CustomerDetails(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }
}

// Run Server main first, then run Client to connect

// Build and verify Server-1.0.0.jar  using terminal :
// /Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java -Dmaven.multiModuleProjectDirectory=/Users/cw/Documents/GitHub/CISC191-FinalProjectTemplate -Djansi.passthrough=true -Dmaven.home=/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/plugins/maven/lib/maven3 -Dclassworlds.conf=/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/plugins/maven/lib/maven3/bin/m2.conf -Dmaven.ext.class.path=/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/plugins/maven/lib/maven-event-listener.jar -javaagent:/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/lib/idea_rt.jar=54206:/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/bin -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/plugins/maven/lib/maven3/boot/plexus-classworlds.license:/Users/cw/Applications/IntelliJ\ IDEA\ Ultimate.app/Contents/plugins/maven/lib/maven3/boot/plexus-classworlds-2.7.0.jar org.codehaus.classworlds.Launcher -Didea.version=2024.1.4 install


// Generate Javadoc on terminal
// (base) cw@Celines-MacBook-Pro CISC191-FinalProjectTemplate % java -jar Server/target/Server-1.0.0.jar
// (base) cw@Celines-MacBook-Pro CISC191-FinalProjectTemplate % java -jar Client/target/Client-1.0.0.jar