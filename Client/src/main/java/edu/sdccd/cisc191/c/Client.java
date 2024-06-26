package edu.sdccd.cisc191.c;

import java.net.*;
import java.io.*;

/**
 * This program opens a connection to a computer specified
 * as the first command-line argument.  If no command-line
 * argument is given, it prompts the user for a computer
 * to connect to.  The connection is made to
 * the port specified by LISTENING_PORT.  The program reads one
 * line of text from the connection and then closes the
 * connection.  It displays the text that it read on
 * standard output.  This program is meant to be used with
 * the server program, DateServer, which sends the current
 * date and time on the computer where the server is running.
 */
//
//public class Client {
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//
//    public void startConnection(String ip, int port) throws IOException {
//        clientSocket = new Socket(ip, port);
//        out = new PrintWriter(clientSocket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//    }
//
//    public CustomerResponse sendRequest() throws Exception {
//        out.println(CustomerRequest.toJSON(new CustomerRequest(1)));
//        return CustomerResponse.fromJSON(in.readLine());
//    }
//
//    public void stopConnection() throws IOException {
//        in.close();
//        out.close();
//        clientSocket.close();
//    }
//    public static void main(String[] args) {
//        Client client = new Client();
//        try {
//            client.startConnection("127.0.0.1", 4444);
//            System.out.println(client.sendRequest().toString());
//            client.stopConnection();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//}//end class Client

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public CustomerResponse sendRequest(CustomerRequest request) throws Exception {
        out.println(CustomerRequest.toJSON(request));
        String response = in.readLine();
        return CustomerResponse.fromJSON(response);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        try {
            String serverIp = "127.0.0.1";
            int serverPort = 4444;

            // List of customer requests with different IDs
            List<CustomerRequest> requests = Arrays.asList(
                    new CustomerRequest(1),
                    new CustomerRequest(2),
                    new CustomerRequest(3),
                    new CustomerRequest(4),
                    new CustomerRequest(5)
            );

            Client client = new Client();
            client.startConnection(serverIp, serverPort);

            // Sending requests and printing responses
            for (CustomerRequest request : requests) {
                System.out.println("Sending request: " + request.toString());
                CustomerResponse response = client.sendRequest(request);
                System.out.println("Response received: " + response.toString());
            }

            client.stopConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
