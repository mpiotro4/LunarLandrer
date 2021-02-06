package com.company;
import java.awt.*;
import java.io.IOException;

/**
 * klasa odpowiedzialna za menu sluzace do laczenia sie z serwerem, rozszerza klase panel
 */
public class ConnectionMenu extends Panel {
    /**
     * Konstruktor w ktorym dodawane sa wszystkie komponenty
     * Po wpisaniu portu, adresu ip i wcisnieciu odpowiedniego przycisku sprawdza czy serwer jest osiagalny, jesli tak aplikacja uruchamia sie
     * w trybie sieciowym
     * @param mainFrame glowna ramka w ktorej znajduja sie metody sluzace do wyswietlania kolejnych okien graficznego interfejsu uzytkownika
     */
    public ConnectionMenu(MainFrame mainFrame){
        this.setLayout(new GridBagLayout());
        Button exitButton = new Button("Wyjście");
        Button offlineButton = new Button("Graj offline");
        Button onlineButton = new Button("Graj online");
        Label ipLabel = new Label("Adres ip serwera: ");
        Label portLabel = new Label("Port: ");
        TextField ipField = new TextField("000.000.000.000");
        TextField portField = new TextField("000000");
        Label connectionLabel = new Label();
        exitButton.addActionListener(event -> System.exit(1));
        offlineButton.addActionListener(event -> {
            try {
                Ranking.loadRanking();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mainFrame.mainMenu();
        });
        onlineButton.addActionListener(event -> {
            try {
                PropertiesLoader.savePortAndIP(Integer.parseInt(portField.getText()),ipField.getText());
                PropertiesLoader.loadConfigFromServer();
            } catch (IOException e) {
                System.out.println("Connection could not be established");
                connectionLabel.setText("Nie udało się ustanowić połączenia");
                this.revalidate();
            }
            if(!Client.checkIfOffline()) {
                mainFrame.mainMenu();
                System.out.println("Connection established");
            }
        });
        this.add(connectionLabel);
        this.add(exitButton);
        this.add(offlineButton);
        this.add(onlineButton);
        this.add(portLabel);
        this.add(portField);
        this.add(ipLabel);
        this.add(ipField);
    }
}
