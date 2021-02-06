package com.company;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Klasa odpowiedzialna za czytanie z plikow konfiguracyjnych.
 * Z pliku Maps.txt odczytywane są wielkosci wedlug ktorych generowana jest mapa. W pliku Config.txt xpoints i ypoints
 * reprezentują wspolrzedne kolejnych wierzchołków wielokata ktorym bedzie wygenerowana mapa (dowolna liczba punktow),
 * a landing sklada sie z czterech wartosci ktore okreslaja wspołrzedne oraz wymiary ladowiska.
 * Program przyjmuje że wspolrzedne znajduja się w kwadracie 100x100 a następnie wszystko przeskalowuje do obecnego
 * rozmiaru okna.
 */

class PropertiesLoader {
    /** Okresla startowa szerokosc okna */
    static int xSize;
    /** Okresla startowa wysokosc okna */
    static int ySize;
    /** Okresla odpowiednia predkosc w plaszczyznie X */
    static int properVx;
    /** Okresla odpowiednia predkosc w plaszczyznie Y */
    static int properVy;
    /** Okresla odpowiednia predkosc w plaszczyznie Y */
    static int fuelAmount;
    /** Okresla ilosc poziomów */
    static int numberOfLevels;
    /** Okresla ilosc statkow */
    static int numberOfLives;
    /** Okresla ilosc punktow przyznawanych za pozostale statki */
    static int bonusPerLife;
    /** Okersla punkt startowy w ktorym pojawia sie gracz */
    static int startPoint;
    /** tablica przechowujaca x'owe wspolrzedne wierzcholkow wielokata bedacych powierzchnia ksiezyca */
    static int[] xPoints;
    /** tabblica przechowujaca y'owe wspolrzedne wierzcholkow wielokata bedacych powierzchnia ksiezcya */
    static int[] yPoints;
    /** tablica przechowujaca x'owe wspolrzedne wierzcholkow wielokata bedacego ladowiskiem */
    static int[] xLanding;
    /** tablica przechowujaca y'owe wspolrzedne wierzcholkow wielokata bedacego ladowiskiem */
    static int[] yLanding;
    /** Okresla maksymalna pionowa predkosc */
    static int maxVy;
    /** Adres ip serwera  */
    static String ipAddress;
    /** Port na ktorym dziala serwer */
    static int port;

    /**
     * Zapisuje do zmiennych dane podane przez uzytkownika w momencie laczenia sie z serwerem
     * @param port numer portu
     * @param ipAddress adres ip serwera
     */
    static void savePortAndIP(int port,String ipAddress){
        PropertiesLoader.port = port;
        PropertiesLoader.ipAddress = ipAddress;
    }

    /**
     *Wczytuje dane z plikow konfiguracyjnych i zapisujaca je do odpowiednich pol w klasie
     */
    static void loadConfig() throws IOException {
        InputStream propertiesFile = new FileInputStream("Config.txt");
        Properties properties = new Properties();
        properties.load(propertiesFile);
        xSize = Integer.parseInt(properties.getProperty("CanvasSizeX"));
        ySize = Integer.parseInt(properties.getProperty("CanvasSizeY"));
        numberOfLevels = Integer.parseInt(properties.getProperty("numberOfLevels"));
        properVx = Integer.parseInt(properties.getProperty("properVx"));
        properVy = Integer.parseInt(properties.getProperty("properVy"));
        fuelAmount = Integer.parseInt(properties.getProperty("fuelAmount"));
        numberOfLives = Integer.parseInt(properties.getProperty("numberOfLives"));
        bonusPerLife = Integer.parseInt(properties.getProperty("bonusPerLife"));
        maxVy = Integer.parseInt(properties.getProperty("maxVy"));
        propertiesFile.close();
    }
    /**
     *Wczytuje dane z plikow konfiguracyjnych serwera i zapisuje je do odpowiednich pol w klasie
     */
    static void loadConfigFromServer() throws IOException {
        String response = Client.getConfig();
        int[] config;
        config = Arrays.stream(response.split("-")).mapToInt(Integer::parseInt).toArray();
        xSize = config[0];
        ySize = config[1];
        properVx = config[2];
        properVy = config[3];
        fuelAmount = config[4];
        numberOfLevels = config[5];
        numberOfLives = config[6];
        bonusPerLife = config[7];
        maxVy = config[8];
    }
    /**
     * Wczytuje wspolrzedne z pliku konfiguracyjnego serwera i zapisuje je do odpowednich tablic
     * @param levelIndex numer poziomu ktory chcemy wczytac z pliku konfiguracyjnego serwera
     */
    static void loadLevelConfigsFromServer(int levelIndex) throws IOException {
        String response = Client.getLevel(levelIndex);
        String[] configs = response.split("-");
        xPoints = Arrays.stream(configs[0].split(" ")).mapToInt(Integer::parseInt).toArray();
        yPoints = Arrays.stream(configs[1].split(" ")).mapToInt(Integer::parseInt).toArray();
        xLanding = Arrays.stream(configs[2].split(" ")).mapToInt(Integer::parseInt).toArray();
        yLanding = Arrays.stream(configs[3].split(" ")).mapToInt(Integer::parseInt).toArray();
        startPoint = Integer.parseInt(configs[4]);
        transformPoints();
    }
    /**
     * Wczytuje wspolrzedne z pliku konfiguracyjnego i zapisuje je do odpowednich tablic
     * @param levelIndex numer poziomu ktory chcemy wczytac z pliku konfiguracyjnego serwera
     */
    static void loadLevelConfigs(int levelIndex) throws IOException {
        InputStream propertiesFile2 = new FileInputStream("Maps.txt");
        Properties mapProperties = new Properties();
        mapProperties.load(propertiesFile2);
        xPoints = Arrays.stream(mapProperties.getProperty("xpoints" + levelIndex).split("-")).mapToInt(Integer::parseInt).toArray();
        yPoints = Arrays.stream(mapProperties.getProperty("ypoints" + levelIndex).split("-")).mapToInt(Integer::parseInt).toArray();
        xLanding = Arrays.stream(mapProperties.getProperty("xlanding" + levelIndex).split("-")).mapToInt(Integer::parseInt).toArray();
        yLanding = Arrays.stream(mapProperties.getProperty("ylanding" + levelIndex).split("-")).mapToInt(Integer::parseInt).toArray();
        startPoint = Integer.parseInt(mapProperties.getProperty("startPoint" + levelIndex));
        transformPoints();
    }
    /**
     * Przekalowywuje wspolrzedne do startowych wymiarow okna z pliku konfiguracyjnego
     */
    private static void transformPoints()
    {
        yPoints = Arrays.stream(yPoints).map(y -> (int)(PropertiesLoader.ySize *0.01*y)).toArray();
        xPoints = Arrays.stream(xPoints).map(x -> (int)(PropertiesLoader.xSize *0.01*x)).toArray();
        xLanding = Arrays.stream(xLanding).map(y -> (int)(PropertiesLoader.xSize *0.01*y)).toArray();
        yLanding = Arrays.stream(yLanding).map(y -> (int)(PropertiesLoader.ySize *0.01*y)).toArray();
        startPoint = (int)(startPoint * 0.01 * PropertiesLoader.xSize);
    }
}


