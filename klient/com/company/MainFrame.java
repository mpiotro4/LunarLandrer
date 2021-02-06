package com.company;
import javax.swing.*;
import java.util.Timer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za tworzenie ramki i obsługe zdarzen pochodzących z graficznego
 * interfejsu użytkownika
 */

class MainFrame extends Frame{
    /** Informuje czy gra jest zapauzowana, true - zapauzowana */
    private boolean pauseButtonPressed = false;
    /** Licznik odpowiedzialny za ruch animacji */
    private Timer timer;
    /** Przechowuje numer obecnego poziomu */
    private int currentLevel;
    /** Przechowuje liczbe pozostalych statkow */
    private int numberOfLives;
    /** Przechowuje nick gracza */
    private String nick;
    /** Przechowuje wynik gracza w trakcie rozgrywki*/
    private int score;

    /**
     * Ustanwia rozmiar okna z pliku konfiguracyjnego, tworzy obiekt klasy timer i wyswietla menu w ktorym gracz laczy sie z serwerem
     */
    MainFrame() throws IOException {
        super("LUNAR LANDER");
        this.setSize(PropertiesLoader.xSize,PropertiesLoader.ySize);
        this.add(new ConnectionMenu(this));
        timer = new Timer();
    }

 /*   private void rankingMenu() throws IOException {
        this.add(new RankingMenu(this));
        this.revalidate();
        if(!Client.isOffline) Ranking.loadRankingFromServer();;
        this.removeAll();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Label label1 = new Label("Najlepsze wyniki");
        this.add(label1);
        label1.setAlignment(Label.CENTER);
        label1.setMaximumSize(new Dimension(100,70));
        Label[] scoreLabels = new Label[5];
        for(int i = 0; i < 5; i++){
            scoreLabels[i] = new Label(((i+1) +". " + Ranking.getScore(i)));
            this.add(scoreLabels[i]);
            scoreLabels[i].setAlignment(Label.CENTER);
            scoreLabels[i].setMaximumSize(new Dimension(100,70));
        }
        Button menuButton = new Button("Powrót");
        menuButton.setMaximumSize(new Dimension(100,70));
        menuButton.addActionListener(event -> mainMenu());
        this.add(menuButton);
        revalidate();
    } */

    /**
     * Wyswietla glowne menu. Najpierw czysci cale okno a nastepnie dodaje kolejne komponenty
     */
    void mainMenu(){
        this.removeAll();
        this.setLayout(new GridBagLayout());
        currentLevel = 1;

        Button startButton = new Button("Start");
        Button exitButton = new Button("Wyjście");
        Button rankingButton = new Button("Najlepsze Wyniki: ");

        startButton.addActionListener(event -> nameMenu());
        exitButton.addActionListener(event -> System.exit(1));
        rankingButton.addActionListener(event -> {
            try {
             //   rankingMenu();
                this.add(new RankingMenu(this));
                this.revalidate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.add(startButton);
        this.add(exitButton);
        this.add(rankingButton);
        revalidate();
    }

    /**
     * Wyswietla menu pokazujace sie po zakonczeniu rozgrywki. Czysci okno i dodaje obiekt klasy EndGameMenu
     */
    private void endGameMenu() throws IOException {
        removeAll();
        this.add(new EndGameMenu(numberOfLives,score,nick,this));
        /*
        this.removeAll();
        this.setLayout(new GridBagLayout());
        if(numberOfLives > 1) score = score + numberOfLives * PropertiesLoader.bonusPerLife;
        if(Client.isOffline) Ranking.SaveScore(score,nick);
        if(!Client.isOffline) Ranking.saveScoreOnServer(nick, score);
        Button okButton = new Button("Ok");
        okButton.addActionListener(event -> mainMenu());
        Label label = new Label("Koniec gry, twój wynik: " + score);
        this.add(label);
        this.add(okButton);
        revalidate(); */
        revalidate();
    }

    /**
     * Wyswietla menu w ktorym gracz podaje swoj nick przed rozpaczeciem rozgrywki
     */
    void nameMenu(){
        this.removeAll();
        this.setLayout(new GridBagLayout());
        TextField nameField = new TextField("Twoje imię");
        Button okButton = new Button("Ok");
        Label label = new Label("Wprowadź imię: ");

        okButton.addActionListener(event -> {
            nick = nameField.getText();
            try {
                loadLevel(currentLevel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            numberOfLives = PropertiesLoader.numberOfLives;
            score = 0;
            this.requestFocus();
        });
        this.add(okButton);
        this.add(label);
        this.add(nameField);
        revalidate();
    }

    /**
     * Wczytuje i wyswietla poziom o zadanym indeksie. Tworzy rowniez nowy watek w ktorym na biezoca sa aktualizowane elementy
     * graficznego interfejsu uzytkownika, oraz sprawdzane jest, czy gracz wyladowal
     * @param levelIndex numer poziomu ktory ma zostac wyswietlony
     */
    private void loadLevel(int levelIndex) throws IOException {
        this.removeAll();
        this.requestFocus();
        int x = this.getSize().width;
        int y = this.getSize().height;
        resetTimer();
        this.removeAll();
        this.setLayout(new BorderLayout());
        timer = new Timer();
        Level level = new Level(levelIndex);
        timer.scheduleAtFixedRate(level.new AnimationTimerTask(), 100, 30);

        Button exitButton = new Button("Wyjście");
        Button pauseButton =  new Button("Pauza");
        Label vy = new Label("999999");
        Label vx = new Label("999999");
        Label fuelLabel = new Label("Paliwo: 99999");
        Label spaceships = new Label("Pozostałe statki: 99");
        Container sideMenu = new Container();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));

        sideMenu.add(vx);
        sideMenu.add(vy);
        sideMenu.add(exitButton);
        sideMenu.add(pauseButton);
        sideMenu.add(spaceships);
        sideMenu.add(fuelLabel);

        pauseButton.addActionListener(event -> pause(level));
        exitButton.addActionListener(event -> {
            level.hasCrushed = true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resetTimer();
            mainMenu();
        });

        this.add(sideMenu,BorderLayout.LINE_END);
        this.add(level,BorderLayout.CENTER);
        MyKeyListener myKeyListener = new MyKeyListener(level);
        this.addKeyListener(myKeyListener);
        this.revalidate();
        this.setSize(x,y);

        new Thread(() -> {
            while (!level.hasSucceeded && !level.hasCrushed){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setColourOfSpeedLabels(vx,vy,level);
                vy.setText("vy: " + level.vy);
                vx.setText("vx: " + level.vx);
                spaceships.setText("Pozostałe statki: " + numberOfLives);
                fuelLabel.setText("Ilość paliwa: " + level.fuel);
                if(level.fuel <= 0) {
                    this.removeKeyListener(myKeyListener); //nie fajnie
                }
            }
            try {
                resetTimer();
                if(level.hasCrushed && numberOfLives <= 1) {
                    endGameMenu();
                }
                if(level.hasCrushed && numberOfLives > 1) {
                    endLevelMenu(false);
                }
                if(level.hasSucceeded) {
                    endLevelMenu(true);
                    score += level.score;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Menu wyswietlane po wyladowaniu informujace gracza czy udalo mu sie wyladwoac pomyslnie
     * @param hasSucceeded informuje czy ladoawanie bylo pomyslnie, true - pomyslnie, false - niepomyslne
     */
    private void endLevelMenu(boolean hasSucceeded){
        this.removeAll();
        this.setLayout(new GridBagLayout());
        if(hasSucceeded) this.add(new Label("Lądowanie zakończone pomyślnie"));
        if(!hasSucceeded) this.add(new Label("Lądowanie zakończone niepomyślnie"));
        Button okButton = new Button("Ok");
        okButton.addActionListener(event -> {
            try {
                if(hasSucceeded) loadNextLevel();
                if(!hasSucceeded) {
                    loadLevel(currentLevel);
                    numberOfLives--;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        this.add(okButton);
        this.revalidate();
    }

    /**
     * ustanawia kolor etykiet wyswietlajace predkosc w zaleznosci od tego czy jest ona dopuszczalna
     * @param vx etykieta predkosci w plaszczyxnie x
     * @param vy etykieta predkosci w plaszczyxnie y
     * @param level obiekt klasy Level z ktorego pobierana jest aktualna predkosc
     */
    private void setColourOfSpeedLabels(Label vx, Label vy, Level level){
        if(Math.abs(level.vx) >PropertiesLoader.properVx) vx.setForeground(Color.RED);
        else vx.setForeground(Color.green);
        if(level.vy > PropertiesLoader.properVy) vy.setForeground(Color.RED);
        else vy.setForeground(Color.green);
    }

    /**
     * Wczytuje kolejny poziom, jesli gracz dotarl do konca wyswietla menu z koncowym wynikiem
     */
    private void loadNextLevel() throws IOException, InterruptedException {
        resetTimer();
        if (currentLevel < PropertiesLoader.numberOfLevels) {
            currentLevel++;
            loadLevel(currentLevel);
        }
        else {
            endGameMenu();
        }
    }

    /**
     * Pauzuje lub wznawia rozgrywke w zaleznosci od obecnego stanu, wywoluje metode w obiekcie klasy Level odpowiednia metode
     * @param level poziom ktory ma zostac zapauzowany lub wznowiony
     */
    private void pause(Level level){
        if(pauseButtonPressed) {
            level.resume();
            pauseButtonPressed = false;
            this.requestFocus();
        }
        else {
            level.pause();
            pauseButtonPressed = true;
        }
    }

    /**
     * Resetuje licznik
     */
    private void resetTimer() {
        timer.cancel();
        timer.purge();
    }

    /**
     * Sprawia ze okno staje sie widoczne oraz umozliwe jego zamkniecie przy uzyciu ikony zamykania okna
     */
    void launchFrame() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(1);
            }
        });
        EventQueue.invokeLater(() -> setVisible(true));
    }
}
