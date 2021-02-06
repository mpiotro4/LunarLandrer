package com.company;
import java.io.IOException;
/**
* Klasa odpwiedziana za wybor odpowiedzi serwera na zadanie klienta
*/
public class ServerCommands {
    /**
     * Na podstawie żądania klienta wybierana jest odpowiednia odpowiedź
     * @param command treść żądania klienta
     * @return odpowiedź serwera
     */
    public static String serverAction(String command) throws IOException {
        String serverMessage;
        String[] commands = command.split("-");
        switch (commands[0]) {
            case "getConfig":
                serverMessage = PropertiesLoader.loadConfig();
                break;
            case "getLevel":
                serverMessage = PropertiesLoader.loadLevel(Integer.parseInt(commands[1]));
                break;
            case "getRanking":
                serverMessage = Ranking.getRanking();
                break;
            case "saveScore":
                Ranking.saveScore(commands[1],commands[2]);
                serverMessage = "Score saved";
                break;
            default:
                serverMessage = "Invalid command";
        }
        return serverMessage;
    }
}
