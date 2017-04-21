import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by bozhidar on 17.04.17.
 *
 * The core class, takes care of creating the graphics window
 * and handling events
 *
 */
public class GUIRunner extends JFrame implements ActionListener {
    //Default output and temporal folders
    private static final String DEFAULT_OUTPUT_PATH = "output",
            DEFAULT_TEMP_DIR = "temp";
    private static final int LOW_MAX_JOBS = 1,
                        HIGH_MAX_JOBS = 5;
    //Swing elements
    private JFileChooser chooseDirectory;
    private JLabel chosenDirectory;
    private JComboBox functions;
    private JButton chooseDirButton, runButton;

    public GUIRunner(String name) {
        //Sets the  display of the exceptions
        ExceptionGUI.display = false;
        //Sets the title of the window
        setTitle(name);

        //Create new content pane
        Container pane = new Container();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        //Add JPanels to the pane
        pane.add(buildFileChooser());
        pane.add(buildOperationRun());

        //Settings
        setContentPane(pane);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Builds a JPanel that contains the components for choosing a file.
     *
     * @return the complete panel
     */
    public JPanel buildFileChooser() {
        //Create new file chooser
        chooseDirectory = new JFileChooser();
        //Set it to current directory
        chooseDirectory.setCurrentDirectory(new File("."));
        chooseDirectory.setDialogTitle("Choose directory");
        //Select only directories
        chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //Accept no files
        chooseDirectory.setAcceptAllFileFilterUsed(false);

        //Create a button
        chooseDirButton = new JButton("Choose directory");
        chooseDirButton.setActionCommand("choose dir");
        chooseDirButton.addActionListener(this);

        //Label for holding the chosen directory
        chosenDirectory = new JLabel("Directory: ...");

        //Create the panel to return
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        //Add elements
        panel.add(chosenDirectory);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(chooseDirButton);

        //Set border to the panel
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    /**
     * Creates a JPanel for all the operations that can be done with the data.
     *
     * @return the complete panel
     */
    public JPanel buildOperationRun() {
        //Create options array
        Object[] options = new String[]{
                "Simple count",
                "Most popular",
                "Most retweeted",
                "Simple count - benchmark"
        };

        //Create the drop-down
        functions = new JComboBox(options);
        functions.setToolTipText("Choose a function to run on the data");
        //Disable it (it would be enabled once there is a chosen directory)
        functions.setEnabled(false);

        //Create button
        runButton = new JButton("Run function");
        runButton.setActionCommand("run function");
        runButton.addActionListener(this);

        //Create panel that will be returned
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        //Add components to the panel
        panel.add(functions);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(runButton);

        //Set a border to the panel
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    /**
     * Handles events.
     *
     * @param actionEvent the event that occured
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            //If the choose directory button has been clicked
            case "choose dir": {
                //Open dialog and take the chosen option
                if (chooseDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    //If a directory has been chosen then
                    //Set the label's text to the direcctory
                    chosenDirectory.setText(chooseDirectory.getSelectedFile().getPath());
                    //Change the button text
                    chooseDirButton.setText("Change directory");
                    //Enable the drop-down
                    functions.setEnabled(true);
                    //Pack the window
                    pack();
                }
                break;
            }

            //If the run function button has been clicked
            case "run function": {
                /**
                 * If no directory has been selected show an error.
                 * I could have set the button as disabled unless a
                 * directory is chosen, but preferred this approach
                 * because it is showing more of the potential of my
                 * program and also is more user-friendly - otherwise
                 * the user my be wondering why the button is inactive.
                 */
                if (chosenDirectory.getText().equals("Directory: ...")) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(null,
                                        "You have not selected directory!",
                                        "No directory error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } catch (InterruptedException | InvocationTargetException e) {
                        ExceptionGUI.displayException(e);
                    }
                } else {
                    //Get the input path
                    String input_path = chosenDirectory.getText();

                    //Switch on the chosen from the drop-down function
                    switch (functions.getItemAt(functions.getSelectedIndex()).toString()) {
                        case "Simple count": {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Execute the simple count function
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                        countHashtags.simpleCount(false);

                                        //Show message on completion
                                        countHashtags.ready("Simple Count");

                                        //Display the results in a table
                                        new ResultDisplayer("Hashtag count", new String[]{"Hashtag", "Occurences"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayException(e);
                                    }
                                }
                            }).start();

                            break;
                        }
                        case "Most popular": {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Execute simple count
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_TEMP_DIR);
                                        countHashtags.simpleCount(false);

                                        //Execute most popular
                                        JobExecutor getMostPopular = new JobExecutor(DEFAULT_TEMP_DIR, DEFAULT_OUTPUT_PATH);
                                        getMostPopular.mostPopular(false);

                                        //Show message on completion
                                        getMostPopular.ready("Sort by popularity");

                                        //Display result in a table
                                        new ResultDisplayer("Most popular", new String[]{"Hashtag", "Occurences"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayException(e);
                                    }
                                }
                            }).start();

                            break;
                        }
                        case "Most retweeted": {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Execute most retweeted
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_TEMP_DIR);
                                        countHashtags.mostRetweeted(false);

                                        //Execute most popular (sort the retweets)
                                        JobExecutor sortRetweeted = new JobExecutor(DEFAULT_TEMP_DIR, DEFAULT_OUTPUT_PATH);
                                        sortRetweeted.mostPopular(false);

                                        //Show message on completion
                                        sortRetweeted.ready("Most retweeted");

                                        //Show results in a table
                                        new ResultDisplayer("Most retweeted", new String[]{"User id", "Retweets"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayException(e);
                                    }
                                }
                            }).start();

                            break;
                        }
                        case "Simple count - benchmark": {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //Create datasets
                                        XYSeriesCollection dataset = new XYSeriesCollection();
                                        XYSeries seriesIncreasing = new XYSeries("Increasing jobs");
                                        XYSeries seriesDecreasing = new XYSeries("Decreasing jobs");

                                        //Populate the datasets
                                        //Increasing the number of jobs
                                        for (int jobs = LOW_MAX_JOBS; jobs <= HIGH_MAX_JOBS; jobs ++) {
                                            long startTime = System.currentTimeMillis();
                                            JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                            countHashtags.simpleCountGraph(false, jobs);
                                            long totalTime = System.currentTimeMillis() - startTime;
                                            seriesIncreasing.add(jobs, totalTime);
                                        }
                                        //Decreasing the number of jobs
                                        for (int jobs = HIGH_MAX_JOBS; jobs >= LOW_MAX_JOBS; jobs --) {
                                            long startTime = System.currentTimeMillis();
                                            JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                            countHashtags.simpleCountGraph(false, jobs);
                                            long totalTime = System.currentTimeMillis() - startTime;
                                            seriesDecreasing.add(jobs, totalTime);
                                        }

                                        //Create parameters for the chart
                                        dataset.addSeries(seriesIncreasing);
                                        dataset.addSeries(seriesDecreasing);
                                        String chartTitle = "Multiple jobs benchmark";
                                        String xAxisLabel = "Max jobs";
                                        String yAxisLabel = "Time (ms)";

                                        //Create cahrt
                                        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                                                xAxisLabel, yAxisLabel, dataset);

                                        //Create chart panel
                                        JPanel chartPanel = new ChartPanel(chart);

                                        //Create a new window with the chart panel
                                        try {
                                            SwingUtilities.invokeAndWait(new Runnable() {
                                                @Override
                                                public void run() {
                                                    JFrame chartWindow = new JFrame("Benchmark results");
                                                    chartWindow.setContentPane(chartPanel);
                                                    chartWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                                                    chartWindow.pack();
                                                    chartWindow.setVisible(true);
                                                }
                                            });
                                        } catch (InterruptedException | InvocationTargetException e) {
                                            ExceptionGUI.displayExceptionWait(e);
                                        }
                                    } catch (IOException e) {
                                        ExceptionGUI.displayException(e);
                                    }
                                }
                            }).start();

                            break;
                        }
                    }
                }
            }
        }
    }
}
