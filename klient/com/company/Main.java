package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        PropertiesLoader.loadConfig();
        MainFrame frame = new MainFrame();
        frame.launchFrame();
    }
}
