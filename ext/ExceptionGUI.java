import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by bozhidar on 17.04.17.
 */
public class ExceptionGUI {
    public static boolean display = true;

    public static void displayExceptionWithWait(Exception e) {
        String type = e.getClass().getName();
        if (display) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null,
                                type + ": " + e.getMessage(),
                                type,
                                JOptionPane.WARNING_MESSAGE);
                    }
                });
            } catch (InterruptedException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        } else {
            System.out.println(e.getMessage());
        }
    }

    public static void displayExceptionWithoutWait(Exception e) {
        String type = e.getClass().getName();
        if (display) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null,
                            type + ": " + e.getMessage(),
                            type,
                            JOptionPane.WARNING_MESSAGE);
                }
            });
        } else {
            System.out.println(e.getMessage());
        }
    }
}
