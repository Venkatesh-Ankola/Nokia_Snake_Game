import javax.swing.*;

public class GameLayout extends JFrame {
    GameLayout(){
        this.add(new GameBoard());
        this.setTitle("Nokia Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
