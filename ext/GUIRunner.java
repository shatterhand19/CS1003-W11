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
 */
public class GUIRunner extends JFrame implements ActionListener {
    private static final String DEFAULT_OUTPUT_PATH = "output",
            DEFAULT_TEMP_DIR = "temp";
    private JFileChooser chooseDirectory;
    private JLabel chosenDirectory;
    private JComboBox functions;
    private JButton chooseDirButton, runButton;

    public GUIRunner(String name) {
        ExceptionGUI.display = false;
        setTitle(name);

        Container pane = new Container();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        //Add JPanels to the pane
        pane.add(buildFileChooser());
        pane.add(buildOperationRun());

        setContentPane(pane);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JPanel buildFileChooser() {
        chooseDirectory = new JFileChooser();
        chooseDirectory.setCurrentDirectory(new File("."));
        chooseDirectory.setDialogTitle("Choose directory");
        chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooseDirectory.setAcceptAllFileFilterUsed(false);

        chooseDirButton = new JButton("Choose directory");
        chooseDirButton.setActionCommand("choose dir");
        chooseDirButton.addActionListener(this);

        chosenDirectory = new JLabel("Directory: ...");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        panel.add(chosenDirectory);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(chooseDirButton);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    public JPanel buildOperationRun() {
        Object[] options = new String[]{
                "Simple count",
                "Most popular",
                "Most retweeted",
                "Simple count - benchmark"
        };

        functions = new JComboBox(options);
        functions.setToolTipText("Choose a function to run on the data");
        functions.setEnabled(false);

        runButton = new JButton("Run function");
        runButton.setActionCommand("run function");
        runButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

        panel.add(functions);
        panel.add(Box.createRigidArea(new Dimension(20, 0)));
        panel.add(runButton);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case "choose dir": {
                if (chooseDirectory.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    chosenDirectory.setText(chooseDirectory.getSelectedFile().getPath());
                    chooseDirButton.setText("Change directory");
                    functions.setEnabled(true);
                    pack();
                }
                break;
            }

            case "run function": {
                if (chosenDirectory.getText().equals("Directory: ...")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(null,
                                    "You have not selected directory!",
                                    "No directory error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } else {
                    String input_path = chosenDirectory.getText();

                    switch (functions.getItemAt(functions.getSelectedIndex()).toString()) {
                        case "Simple count": {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                        countHashtags.simpleCount(false);
                                        countHashtags.ready("Simple Count");

                                        new ResultDisplayer("Hashtag count", new String[]{"Hashtag", "Occurences"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayExceptionWithoutWait(e);
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
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_TEMP_DIR);
                                        countHashtags.simpleCount(false);

                                        JobExecutor getMostPopular = new JobExecutor(DEFAULT_TEMP_DIR, DEFAULT_OUTPUT_PATH);
                                        getMostPopular.mostPopular(false);

                                        getMostPopular.ready("Sort by popularity");

                                        new ResultDisplayer("Most popular", new String[]{"Hashtag", "Occurences"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayExceptionWithoutWait(e);
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
                                        JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_TEMP_DIR);
                                        countHashtags.mostRetweeted(false);


                                        JobExecutor sortRetweeted = new JobExecutor(DEFAULT_TEMP_DIR, DEFAULT_OUTPUT_PATH);
                                        sortRetweeted.mostPopular(false);

                                        sortRetweeted.ready("Most retweeted");
                                        new ResultDisplayer("Most retweeted", new String[]{"User id", "Retweets"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayExceptionWithoutWait(e);
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
                                        XYSeriesCollection dataset = new XYSeriesCollection();
                                        XYSeries seriesIncreasing = new XYSeries("Increasing jobs");
                                        XYSeries seriesDecreasing = new XYSeries("Decreasing jobs");
                                        for (int jobs = 1; jobs < 6; jobs ++) {
                                            long startTime = System.currentTimeMillis();
                                            JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                            countHashtags.simpleCountGraph(false, jobs);
                                            long totalTime = System.currentTimeMillis() - startTime;
                                            seriesIncreasing.add(jobs, totalTime);
                                        }
                                        for (int jobs = 5; jobs > 0; jobs --) {
                                            long startTime = System.currentTimeMillis();
                                            JobExecutor countHashtags = new JobExecutor(input_path, DEFAULT_OUTPUT_PATH);
                                            countHashtags.simpleCountGraph(false, jobs);
                                            long totalTime = System.currentTimeMillis() - startTime;
                                            seriesDecreasing.add(jobs, totalTime);
                                        }

                                        dataset.addSeries(seriesIncreasing);
                                        dataset.addSeries(seriesDecreasing);
                                        String chartTitle = "Multiple jobs benchmark";
                                        String xAxisLabel = "Max jobs";
                                        String yAxisLabel = "Time (ms)";

                                        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                                                xAxisLabel, yAxisLabel, dataset);

                                        JPanel chartPanel = new ChartPanel(chart);

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
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                        //countHashtags.ready("Simple Count benchmark");

                                        //new ResultDisplayer("Hashtag count", new String[]{"Hashtag", "Occurences"});
                                    } catch (IOException e) {
                                        ExceptionGUI.displayExceptionWithoutWait(e);
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
