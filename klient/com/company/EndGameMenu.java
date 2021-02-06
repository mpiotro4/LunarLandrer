package com.company;
import java.awt.*;
import java.io.IOException;

/**
 * klasa odpowiedzialna za menu wyswietlajace sie po zakonczeniu rozgrywyki. Wyswietla wynik koncowy gracza i umozliwa
 * powrot do menu glownego
 */
public class EndGameMenu extends Panel {
    /**
     * Konstruktor w ktorym dodawane sa komponenty i obliczany koncowy wynik gracza
     * @param numberOfLives liczba statkow gracza
     * @param score wynik gracza
     * @param nick nick gracza
     * @param mainFrame glowna ramka programu sluzaca do wywolania odpowiednich metod w momencie wcisnieca przycisku
     */
    public EndGameMenu(int numberOfLives, int score, String nick, MainFrame mainFrame) throws IOException {
        this.removeAll();
        this.setLayout(new GridBagLayout());
        if(numberOfLives > 1) score = score + numberOfLives * PropertiesLoader.bonusPerLife;
        if(Client.isOffline) Ranking.SaveScore(score,nick);
        if(!Client.isOffline) Ranking.saveScoreOnServer(nick, score);
        Button okButton = new Button("Ok");
        okButton.addActionListener(event -> mainFrame.mainMenu());
        Label label = new Label("Koniec gry, twój wynik: " + score);
        this.add(label);
        this.add(okButton);
        revalidate();
    }
}
