import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by bozhidar on 17.04.17.
 *
 * Displays a table with the data from the part-r-00000 file
 * in the output directory.
 *
 */
public class ResultDisplayer extends JFrame {
    public ResultDisplayer(String title, String[] headers) {
        setTitle(title);

        try {
            //Read the data from the file
            Object[][] data = readData(headers.length);

            //Create a new table
            JTable table = new JTable(data, headers);
            table.setPreferredScrollableViewportSize(new Dimension(300, 500));
            table.setFillsViewportHeight(true);

            //Create a new scroll pane
            JScrollPane pane = new JScrollPane(table);

            //Add the scroll pane to a normal JPanel
            JPanel panelPane = new JPanel();
            panelPane.add(pane);

            //Set settings
            setContentPane(panelPane);
            pack();
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (FileNotFoundException e) {
            dispose();
            ExceptionGUI.displayExceptionWait(e);
        }
    }

    /**
     * Reads the data from output file.
     *
     * @param columns is the number of columns the input file has
     * @return the data formatted as a 2D array
     * @throws FileNotFoundException
     */
    private Object[][] readData(int columns) throws FileNotFoundException {
        //Create a scanner for the file
        String input_path = "output/part-r-00000";
        Scanner scanner = new Scanner(new File(input_path));

        //Add the data here
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(new ArrayList<>());

        int row = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //Scan the line
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                //Add to the row
                data.get(row).add(lineScanner.next());
                //If this has the exact number of columns, go to next line
                if (data.get(row).size() == columns) {
                    row++;
                    data.add(new ArrayList<>());
                }
            }
        }

        //Convert them to object array
        Object[][] finalData = new Object[data.size() - 1][]; //The last row is empty, so omit it
        for (int i = 0; i < data.size() - 1; i++) {
            finalData[i] = data.get(i).toArray();
        }

        return finalData;
    }

}
