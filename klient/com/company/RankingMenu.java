package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Klasa odpowiadajaca z graficzny interfejs uzytkownika wyswietlajacy liste najlepszych wynikow. Rozszerza klase Panel
 */
public class RankingMenu extends Panel {
    /**
     * @param mainFrame glowna ramka w ktorej znajduja sie metody do przelaczania sie pomiedzy oknami graficznego
     * interfejsu uzytkownika
     * @throws IOException
     */
    public RankingMenu(MainFrame mainFrame) throws IOException {

        if(!Client.isOffline) Ranking.loadRankingFromServer();;
        mainFrame.removeAll();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Label label1 = new Label("Najlepsze wyniki");
        this.add(label1);
        label1.setAlignment(Label.CENTER);
        Label[] scoreLabels = new Label[5];
        for(int i = 0; i < 5; i++){
            scoreLabels[i] = new Label(((i+1) +". " + Ranking.getScore(i)));
            this.add(scoreLabels[i]);
        }
        Button menuButton = new Button("Powrót");
        menuButton.addActionListener(event -> mainFrame.mainMenu());
        this.add(menuButton);
    }
}
