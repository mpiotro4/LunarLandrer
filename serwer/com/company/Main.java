package com.company;

import java.io.IOException;
import java.net.InetAddress;

public class Main {
    /**
     * W momencie uruchomienia programu wczytywany jest ranking z pliku, tworzony obiekt klasy serwewr i wyswietlany w konsoli jest adres ip serwera
     * oraz numer portu a nastepnie uruchamiany jest glowny watek serwera
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Ranking.loadRanking();
        Server server = new Server();
        System.out.println("IP adress: " + InetAddress.getLocalHost());
        System.out.println("Port: " + PropertiesLoader.port);
        server.runServer();
    }
}
