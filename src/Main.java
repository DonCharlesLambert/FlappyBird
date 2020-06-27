import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;


class Main{
    public static void main(String[] args) throws InterruptedException{
        //creates the game
        Game window = new Game();
    }


}

class Game extends JFrame implements KeyListener {
    JPanel panel;
    FlappyBird bird;
    ArrayList<Pillar> pillars;
    static int WIDTH = 288;
    static int HEIGHT = 512;

    Game() throws InterruptedException{
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setVisible(true);
        //terminates the program when the close button on the window is pressed
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(this);


        panel = new JPanel(null);
        add(panel);
        bird = new FlappyBird("red");
        panel.add(bird.getBird());
        pillars = new ArrayList<>();
        pillars.add(createPillar(panel));

        //JLabel info = createImage("message.png", 44, 0, panel);
        JLabel base = createImage("base.png", 0, 400);
        panel.setComponentZOrder(base, 0);

        JLabel bg = createImage("background-day.png", 0, 0);
        gameLoop();
    }

    private void gameLoop() throws InterruptedException{
        while(bird.isAlive()){
            bird.animate();
            animatePillars();
            pillarCycle();
            checkCollision();
            Thread.sleep(50);
            pack();
        }
        JLabel gameOver = createImage("gameover.png", 44, 250);
        panel.setComponentZOrder(gameOver, 0);
        pack();
    }

    public void keyPressed(KeyEvent e) {
        bird.jump();
    }

    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped");
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }

    private void gameOver(){
        JLabel gameOver = createImage("gameover.png", 44, 250);
        panel.setComponentZOrder(gameOver, 0);
        pack();
    }

    private JLabel createImage(String filename, int x, int y) {
        JLabel img = new JLabel(new ImageIcon("img/" + filename));
        Dimension size = img.getPreferredSize();
        img.setBounds(x, y, size.width, size.height);
        panel.add(img);
        return img;
    }

    //createPillars
    private void animatePillars(){
        for(Pillar pillar: pillars){
            pillar.animate();
        }
    }

    private void pillarCycle(){
        boolean add = false;
        for(Pillar p: pillars){
            if(139 < p.getPosition() && p.getPosition() < 144){
                add = true;
            }
        }
        if (add){
            pillars.add(createPillar(panel));
        }
    }

    private Pillar createPillar(JPanel panel){
        Pillar p = new Pillar(WIDTH, (int)(Math.random() * 200 + 200));
        JLabel t = p.getTopPillar();
        JLabel b = p.getBottomPillar();
        panel.add(t);
        panel.add(b);
        panel.setComponentZOrder(t, 1);
        panel.setComponentZOrder(b, 1);
        return p;
    }

    private void checkCollision(){
        if(pillars.size() > 1) {
            Pillar p = pillars.get(pillars.size() - 2);
            if (bird.getPosition() - 7 < p.getPosition() && p.getPosition() < bird.getPosition() + 7) {
                if (bird.getAltitude() > p.getBottomHeight() || bird.getAltitude() < p.getTopHeight()) {
                    bird.die();
                }
            }
        }
    }
}

class Pillar{
    private JLabel topPillar;
    private JLabel bottomPillar;
    private int[] location;
    private int SPEED = -5;
    private int SPACE = 450;

    Pillar(int x, int y){
        this.location = new int[]{x, y - SPACE, y};
        this.topPillar = createImage(location[0], location[1], true);
        this.bottomPillar = createImage(location[0], location[2], false);
    }

    public JLabel getTopPillar(){
        return topPillar;
    }

    public JLabel getBottomPillar(){
        return bottomPillar;
    }

    public int getPosition(){
        return location[0];
    }

    public int getTopHeight(){
        return location[1] + topPillar.getPreferredSize().height;
    }
    public int getBottomHeight(){
        return location[2];
    }

    public void animate(){
        move(SPEED);
    }

    private void move(int x){
        location[0] += x;
        topPillar.setBounds(location[0], location[1], topPillar.getPreferredSize().width, topPillar.getPreferredSize().height);
        bottomPillar.setBounds(location[0], location[2], bottomPillar.getPreferredSize().width, bottomPillar.getPreferredSize().height);
    }

    private static JLabel createImage(int x, int y, boolean flip){
        JLabel img;
        if (flip){
            img = new JLabel(new ImageIcon("img/pipe-green-flip.png"));
        }else{
            img = new JLabel(new ImageIcon("img/pipe-green.png"));
        }
        Dimension size = img.getPreferredSize();
        img.setBounds(x, y, size.width, size.height);
        return img;
    }
}

class FlappyBird{
    private JLabel bird;
    private int[] location = {100, 250};
    private String[] images;
    private int animNo = 0;
    private int GRAVITY = 7;
    private int JUMP = -7;
    private int jumpFrames = 0;
    private boolean alive = true;

    FlappyBird(String color){
        this.images = new String[]{color + "bird-downflap.png", color + "bird-midflap.png", color + "bird-upflap.png"};
        this.bird = createImage(images[0], location[0], location[1]);
    }

    public JLabel getBird(){
        return bird;
    }

    public int getPosition(){
        return location[0];
    }

    public int getAltitude(){
        return location[1];
    }

    public void animate(){
        if(jumpFrames > 0) {
            move(0, JUMP);
            jumpFrames--;
        }else if(getAltitude() <= 370){
            move(0, GRAVITY);
        }else{
            die();
        }
        nextImage();
    }

    public void jump(){
        jumpFrames = 4;
    }

    public boolean isAlive(){
        return alive;
    }

    public void die(){
        alive = false;
    }

    private void nextImage(){
        animNo = (animNo + 1) % images.length;
        bird.setIcon(new ImageIcon("img/" + images[animNo]));
    }

    private void changeImage(String filename){
        bird.setIcon(new ImageIcon("img/" + filename));
    }

    private static JLabel createImage(String filename, int x, int y){
        JLabel img = new JLabel(new ImageIcon("img/" + filename));
        Dimension size = img.getPreferredSize();
        img.setBounds(x, y, size.width, size.height);
        return img;
    }


    private void move(int x, int y){
        location[0] += x;
        location[1] += y;
        bird.setBounds(location[0], location[1], bird.getPreferredSize().width, bird.getPreferredSize().height);
    }
}