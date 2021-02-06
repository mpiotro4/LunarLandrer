package com.company;
import java.io.*;
import java.net.Socket;

/**
 * Klasa odpowiedzialna za watek w ktorym serwer obsluguje zadania klientow
 */
public class ServerThread implements Runnable {

    private Socket socket;
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    /**
     * Odczytuje zadanie klienta i wywołuję metode ktora ma za zadnie na nie odpowiedziec
     */
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(10);
                InputStream inputStream = socket.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                String fromClient = bufferedReader.readLine();
                if (fromClient != null) {
                    System.out.println("From client: " + fromClient);
                    String serverRespond = ServerCommands.serverAction(fromClient);
                    printWriter.println(serverRespond);
                    printWriter.flush();
                    System.out.println("Server respond: " + serverRespond);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Connection lost");
        }
    }
}
