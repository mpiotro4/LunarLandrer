package com.company;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Klasa odpowiedzialna za obsługe zdarzen z klawiatury
 */
public class MyKeyListener implements KeyListener {
    /** obiekt który ma byc modyfikowany poprzez zdarzenia z klawiatury */
    private Level level;

    MyKeyListener(Level level){
        this.level = level;
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    /**
     * Obsluguje zdarzenia z klawiatury w przypadku przytrzymania przycisku. Pod wplywem zdarzenia dpowiednia
     * zmienna reprezentujaca predkosc w obiekcie Klasy Level jest modifykowana. Poza tym za kazdym wcisnieciem jakiego
     * kolwiek przycisku dekrementowana jest zmienna reprezentująca poziom paliwa.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            if(level.vy > -10)
            level.vy = level.vy - 2;}
        if (key == KeyEvent.VK_LEFT) {
            level.vx = level.vx - 2;}
        if (key == KeyEvent.VK_RIGHT) {
            level.vx = level.vx + 2;}
        level.fuel --;
    }
    @Override
    /**
     * Obsluguje zdarzenia z klawiatury w przypadku puszczenia przycisku. Pod wplywem zdarzenia odpowiednia
     * zmienna reprezentujaca prędkosc w obiekcie Klasy Level jest modifykowana.
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            level.vy = level.vy-3;
        }
    }
}
