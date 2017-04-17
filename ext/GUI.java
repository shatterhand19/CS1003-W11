import javax.swing.*;

/**
 * Created by bozhidar on 17.04.17.
 */
public class GUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIRunner("Process hashtags");
            }
        });
    }
}
