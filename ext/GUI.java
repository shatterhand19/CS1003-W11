import javax.swing.*;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Main class that runs the GUI window.
 *
 */
public class GUI {
    public static void main(String[] args) {
        //Create and display window
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIRunner("Process hashtags");
            }
        });
    }
}
