import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by bozhidar on 17.04.17.
 */
public class ResultDisplayer extends JFrame {
    public ResultDisplayer(String title, String[] headers) {
        setTitle(title);


        try {
            Object[][] data = readData(headers.length);
            JTable table = new JTable(data, headers);
            table.setPreferredScrollableViewportSize(new Dimension(300, 500));
            table.setFillsViewportHeight(true);

            JScrollPane pane = new JScrollPane(table);

            JPanel panelPane = new JPanel();
            panelPane.add(pane);

            setContentPane(panelPane);
            pack();
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (FileNotFoundException e) {
            dispose();
            ExceptionGUI.displayExceptionWithWait(e);
        }


    }

    private Object[][] readData(int columns) throws FileNotFoundException {
        String input_path = "output/part-r-00000";
        Scanner scanner = new Scanner(new File(input_path));

        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(new ArrayList<>());

        int row = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            while (lineScanner.hasNext()) {
                data.get(row).add(lineScanner.next());
                if (data.get(row).size() == columns) {
                    row++;
                    data.add(new ArrayList<>());
                }
            }
        }

        Object[][] finalData = new Object[data.size() - 1][];
        for (int i = 0; i < data.size() - 1; i++) {
            finalData[i] = data.get(i).toArray();
        }

        return finalData;
    }

}
