package com.company;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Klasa odpowiedzialna za obsluge zadan klientow. W momencie otrzymania zadania tworzy nowy watek.
 */
public class Server {
    int port;
    /**
     * Konstruktor w ktorym przydzielany jest numer portu z pliku konfiguracyjnego
     */
    public Server() throws IOException {
        PropertiesLoader.loadPort();
        port = PropertiesLoader.port;
    }
    /**
     * Serwer oczekuje na zgloszenia od klientow. W momencie gdy pojawi sie klient nastepuje utworzenie nowego watku
     * w ktorym klient jest obslugiwany a serwer dalej oczekuje na kolejne klienty
     */
    public void runServer() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is up! Waiting for connections...");
        while (true){
            Thread.sleep(10);
            Socket socket = serverSocket.accept();
            new Thread(new ServerThread(socket)).start();
        }
    }
}
