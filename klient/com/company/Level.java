package com.company;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.TimerTask;

/**
 * Klasa odpowiedzialna za rysowanie
 */
public class Level extends Canvas {
    /** Wynik gracza w obecnym poziomie */
    int score;
    /** informuje czy ladowanie bylo pomuslnie */
    boolean hasSucceeded = false;
    /** informuje czy ladowanie bylo niepomyslne */
    boolean hasCrushed = false;
    /** informuje czy gra jest zapauzowana */
    boolean isPaused;
    /** obecny poziom paliwa */
    float fuel;
    /** obiekt sluzacy do animacji */
    private BufferStrategy bs = null;
    /** predkosc w plaszczyznie y */
    int vy;
    /** predkosc w plaszczyznie x */
    int vx;
    /** polozenie w plaszczyxnie x*/
    int x;
    /** polozenie w plaszczyxnie y */
    int y;

    /**
     * W zaleznosci od tego czy serwer jest osiagalny wczytuje pliki konfiguracyjne z serwera lub z plikow lokalnych
     * @param levelIndex numer poziomu ktory ma zostac narysowany
     */
    Level(int levelIndex) throws IOException {
        if(!Client.isOffline) PropertiesLoader.loadLevelConfigsFromServer(levelIndex);
        if(Client.isOffline) PropertiesLoader.loadLevelConfigs(levelIndex);
        fuel = PropertiesLoader.fuelAmount;
        x = PropertiesLoader.startPoint;
    }
    /**
     * Modyfikuje pozycje statku i symuluje fizyke
     */
    private void modifyLocation(){
        y = y + vy;
        if (vy < PropertiesLoader.maxVy) vy++;
        if(vx > PropertiesLoader.maxVy) vx = PropertiesLoader.maxVy;
        if(vx < -PropertiesLoader.maxVy) vx = -PropertiesLoader.maxVy;
        x = x + vx;
        if (vx > 1) vx--;
        if (vx < -1) vx++;
    }

    public void addNotify() {
        super.addNotify();
        createBufferStrategy(2);
        bs = getBufferStrategy();
    }

    /**
     *Rysuje wielokat bedacy powierzchnia ksiezyca, ladowisko oraz statek i skaluje calosci wraz z rozmiarem okna
     */
    private void updateOffscreen(Graphics g) {
        Shape moon = new Polygon(PropertiesLoader.xPoints,PropertiesLoader.yPoints,PropertiesLoader.xPoints.length);
        Shape landing = new Polygon(PropertiesLoader.xLanding,PropertiesLoader.yLanding,PropertiesLoader.xLanding.length);
        Rectangle2D player = new Rectangle2D.Float(x, y, 25, 25);
        Graphics2D g2d = (Graphics2D) g;
        setBackground(new Color(65,74,76));
        g.clearRect(0, 0, getWidth(), getHeight());

        AffineTransform saveTransform = g2d.getTransform();
        AffineTransform scaleMatrix = new AffineTransform();
        float sx =(1f+(getSize().width-PropertiesLoader.xSize)/(float)PropertiesLoader.xSize);
        float sy =(1f+(getSize().height-PropertiesLoader.ySize)/(float)PropertiesLoader.ySize);
        scaleMatrix.scale(sx, sy);
        g2d.setTransform(scaleMatrix);

        g2d.setColor(Color.black);
        g2d.fill(player);

        g2d.setColor(Color.gray);
        g2d.fill(landing);

        g.setColor(Color.lightGray);
        g2d.fill(moon);

        g2d.setTransform(saveTransform);

        detectCollision(moon,landing,player);
    }
    /**
     * Wykrywa kolizje i okresla czy lodwanie bylo pomyslne
     * @param moon ksztalt ksiezyca
     * @param landing ksztalt ladowiska
     * @param player ksztalt gracza
     */
    private void detectCollision(Shape moon, Shape landing, Rectangle2D player){
        if((moon.intersects(player) || (landing.intersects(player)) && Math.abs(vy) > PropertiesLoader.properVx)) {
            hasCrushed = true;
            hasSucceeded = false;
        }
        if(landing.intersects(player) && Math.abs(vy) <= PropertiesLoader.properVy && Math.abs(vx) <= Math.abs(PropertiesLoader.properVx)) {
            hasSucceeded = true;
            hasCrushed = false;
        }
    }

    /**
     * Pauzuje gre poprzez nadanie zmiennej isPaused wartosci true
     */
    public void pause() {
        isPaused = true;
    }
    /**
     * Wznawia gre poprzez nadanie zmiennej isPaused wartosci false
     */
    public void resume() {
        isPaused = false;
    }

    /**
     * Zmienia pozycja wraz z statku w wyniku czego powstaje animacja oraz dodaje punkty
     */
    public class AnimationTimerTask extends TimerTask {
        int i = 0;
        public void run() {
   /*         try {
                if(vy !=0)  Thread.sleep(70-3*Math.abs(vy));
                modifyLocation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } */
            // Prepare for rendering the next frame
            if(!isPaused) {
                if (i == 2) {
                    modifyLocation();
                    i = 0;
                }
                i++;
            }
            // Render single frame
            do {
                // The following loop ensures that the contents of the drawing buffer
                // are consistent in case the underlying surface was recreated
                do {
                    // Get a new graphics context every time through the loop
                    // to make sure the strategy is validated
                    Graphics graphics = bs.getDrawGraphics();
                    updateOffscreen(graphics);// Render to graphics
                    graphics.dispose(); // Dispose the graphics
                    if(!isPaused) score++;
                    // Repeat the rendering if the drawing buffer contents were restored
                } while (bs.contentsRestored());
                // Display the buffer
                bs.show();
                // Repeat the rendering if the drawing buffer was lost
            } while (bs.contentsLost());
        }
    }
}

