package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;
/**
 * Klasa odpowiedzialna za obsluge listy najlepszych wynikow
 */
public class Ranking {
    /** Lista w której przechowywane są wyniki w formacie "Nick"-"Wynik" */
    static ArrayList<String> ranking;
    /**
     * Wczytuje ranking z pliku konfiguracyjnego i zapisuje go do listy. Na samym koncu lista jest sortowana
     */
    public static void loadRanking() throws IOException {
        InputStream propertiesFile = new FileInputStream("Ranking.txt");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        ranking = new ArrayList<>();
        for(int i = 0 ; i < 5 ; i++){
            ranking.add(properties.getProperty("nick" + (i+1)) + "-" + properties.getProperty("score" + (i+1)));
        }
        propertiesFile.close();
        ranking.sort(new MyComparator());
    }
    /**
     * Na zadanie klienta wysyla liste najelpszych wyników
     * Metoda pakuje cala liste do jednej lini tekstu w ktorej kolejne pary nick-wynik sa odzielone przecinkiem
     * @return linia tekstu skladajaca się z nazw graczy i ich wynikow
     */
    static String getRanking() throws IOException {
        InputStream propertiesFile = new FileInputStream("Ranking.txt");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        String response = "";
        for(int i = 0 ; i < 5 ; i++)
        {
            if(i<4) response+= ranking.get(i) + ", ";
            if(i==4) response+= ranking.get(i);
        }
        return response;
    }
    /**
     * Zapisuje wynik na zadanie kilienta. Po zapisaniu sortuje liste i usuwa ostatni wynik
     * @param Nick nick gracza
     * @param score wynik uzyskany przez gracza
     */
    public static void saveScore(String Nick, String score) throws IOException {
        ranking.add(Nick + "-" + score);
        ranking.sort(new MyComparator());
        ranking.remove(ranking.size()-1);
        SaveInFile();
    }
    /**
     * Implementacja interfejsu funkcyjnego klasy Comparator z biblioteki standardowej
     */
    static class MyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2){
            Integer a = Integer.parseInt(o1.split("-")[1]);
            Integer b = Integer.parseInt(o2.split("-")[1]);
            return -a.compareTo(b);
        }
    }
    /**
     * Zapisuje liste najlepszych wynikow do pliku.
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
}
