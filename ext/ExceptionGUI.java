import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by bozhidar on 17.04.17.
 *
 * This class provides methods for displaying popups with error messages
 *
 */
public class ExceptionGUI {
    //This variable is a flag that shows whether to display a window
    //or to print the exception in the console
    public static boolean display = true;

    /**
     * This popup would stop the current thread for until it is not closed down.
     *
     * @param e the exception to be displayed
     */
    public static void displayExceptionWait(Exception e) {
        //Get the type of the exception
        String type = e.getClass().getName();

        if (display) {
            try {
                //Start a new thread task
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        //Display the error message
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
            //If display is set to false
            System.out.println(e.getMessage());
        }
    }

    /**
     * This popup would NOT stop the current thread for until it is not closed down.
     *
     * @param e the exception to be displayed
     */
    public static void displayException(Exception e) {
        //Get the type of the exception
        String type = e.getClass().getName();

        if (display) {
            //Start a new thread task
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //Display the error message
                    JOptionPane.showMessageDialog(null,
                            type + ": " + e.getMessage(),
                            type,
                            JOptionPane.WARNING_MESSAGE);
                }
            });
        } else {
            //If display is set to false
            System.out.println(e.getMessage());
        }
    }
}
