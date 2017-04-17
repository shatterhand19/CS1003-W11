import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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
                "Most popular"
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
                    }
                }
            }
        }
    }
}
