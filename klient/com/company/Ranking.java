package com.company;

import java.io.*;
import java.util.*;

/**
 * Klasa odpowiedzialna za liste najlepszych wynikow
 */
public class Ranking {
    /** Lista przechowujaca najlepsze wyniki w formacie "nick-wynik" */
    static ArrayList<String> ranking;

    /**
     * Umozliwia pozysyskiwanie kolejnych pozycji z listy o zadanym indeksie
     * @param index numer na liscie wyniku ktory chcemy pozyskac
     * @return linia tekstu w formacie "nick: wynik" ktora pozniej zostaje wyświetlona na liscie w menu uzytkownika
     */
    static String getScore(int index){
        return ranking.get(index).split("-")[0] + ": " + ranking.get(index).split("-")[1];
    }

    /**
     * Zapisuje wyniki na liscie. Od razu sortuje liste i usuwa nadmiarowy wynik ktory
     * nie zmiescil się w rankingu
     * @param score wynik uzyskany przez gracza
     * @param nick nick gracz
     */
    static void SaveScore(int score, String nick) throws IOException {
        ranking.add(nick + "-" + score);
        ranking.sort(new MyComparator());
        ranking.remove(ranking.size()-1);
        SaveInFile();
    }

    /**
     *Zapisuje cala liste wyników do pliku konfiguracyjnego
     */
    static void SaveInFile() throws IOException {
        InputStream propertiesFile = new FileInputStream("Ranking.txt");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        for(int i=0;i<5;i++) {
            properties.setProperty("nick" + (i+1), ranking.get(i).split("-")[0]);
            properties.setProperty("score" + (i+1), ranking.get(i).split("-")[1]);
        }
        properties.store(new FileOutputStream("Ranking.txt"), null);
        propertiesFile.close();
    }

    /**
     * Pobiera liste najlepszych wynikow z serwera i zapisuje do lokalnej listy.
     * Dzieli linie tekstu otrzymana od serwera na kolejne pozycje na liście
     */
    public static void loadRankingFromServer() throws IOException {
        ranking = new ArrayList<>();
        String fromServer = Client.getRanking();
        ranking.addAll(Arrays.asList(fromServer.split(", ")).subList(0, 5));
    }

    /**
     * Formuluje linie tekstu w celu zapisania wyniku na serwerze. Wywoluje metode odpowiedzialna za
     * zapis na serwerze
     * @param nick nazwa gracza
     * @param score wynik uzyskany przez gracza
     */
    public static void saveScoreOnServer(String nick, int score) throws IOException {
        String request = "";
        request+= nick + "-" + score;
        Client.saveScore(request);
    }

    /**
     * Wczytuje dane z pliku konfiguracyjnego i zapisuje je do listy
     */
    public static void loadRanking() throws IOException {
        InputStream propertiesFile = new FileInputStream("Ranking.txt");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        ranking = new ArrayList<>();
        for(int i = 1 ; i <= 5 ; i++){
            ranking.add(properties.getProperty("nick"+i)+"-"+properties.getProperty("score"+i));
        }
/*        ranking.add(properties.getProperty("nick1") + "-" + properties.getProperty("score1"));
        ranking.add(properties.getProperty("nick2") + "-" + properties.getProperty("score2"));
        ranking.add(properties.getProperty("nick3") + "-" + properties.getProperty("score3"));
        ranking.add(properties.getProperty("nick4") + "-" + properties.getProperty("score4"));
        ranking.add(properties.getProperty("nick5") + "-" + properties.getProperty("score5")); */
        propertiesFile.close();
        ranking.sort(new MyComparator());
    }

    /**
     * Implementacja komparatora z biblioteki standardowej w celu sortowania listy wynikow
     */
    static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2){
            Integer a = Integer.parseInt(o1.split("-")[1]);
            Integer b = Integer.parseInt(o2.split("-")[1]);
            return -a.compareTo(b);
        }
    }
}
