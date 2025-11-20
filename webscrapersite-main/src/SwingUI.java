import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.*;
// copuy paste

public class SwingUI {
    private JFrame mainFrame;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JMenu file, edit, help;
    private JMenuItem cut, copy, paste, selectAll;
    private JTextArea searchTermArea; //search term input area
    private JTextArea linkArea; //link input area
    private JTextArea leftTextArea; //left text area 
    private JTextArea rightTextArea; //right text area 
    private final int WIDTH = 800;
    private final int HEIGHT = 700;
    public static boolean on = false;
    public String links = "";
    public String searchTerm = "";


    public SwingUI() {
        prepareGUI();
    }

    public static void main(String[] args) {
        SwingUI SwingUI = new SwingUI();
        SwingUI.showEventDemo();

    }

    public void Finder(String input) {

        try {
            // Clear previous things, not really needed
            links = "";
            leftTextArea.setText("");
            rightTextArea.setText("");
            
            //prints hello to let me know it started
            System.out.print("hello \n");
            URI uri = new URI(input);
            URL url = uri.toURL();


            //opens connection to the site
            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(urlc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("href=")) {

                        // varibles for quote indexes
                        //hDQ is double quote, and single quote is hsq
                        int hDQ = line.indexOf("href=\"");
                        int hSQ = line.indexOf("href='");
                        int start;
                        char cQ;

                        // 2 patterns work, I cover logic for having a double quote at the end or a single by seeig if the closing and opening quotes are single or double
                        if (hDQ != -1) {

                            start = hDQ + 6;
                            cQ = '"';
                        } else if (hSQ != -1) {

                            start = hSQ + 6;
                            cQ = '\'';
                        } else {
                            //kinda just breaks without this. could do else if else
                            continue;
                        }

                        if (start < line.length()) {
                            String urlC = line.substring(start);
                            //starts the substring 
                            int end = urlC.indexOf(cQ);
                            //sbustrings and checks if it didn't return -1, or something wrong, and it check if it didn't start with //
                            if (end != -1 || !urlC.substring(0, 2).equals("//")) {
                                String foundLink = urlC.substring(0, end);
                                links += foundLink + "\n";
                                leftTextArea.setText(links); // Update the text area 
                                if (foundLink.contains(searchTerm)) {
                                    String currentKeywords = rightTextArea.getText();
                                    currentKeywords += foundLink + "\n";
                                    rightTextArea.setText(currentKeywords);
                                }
                            }
                            //check for //, so that it can substring it out.
                            else if (end != -1 || urlC.substring(0, 2).equals("//")) {
                                String foundLink = urlC.substring(2, end);
                                links += foundLink + "\n";
                                leftTextArea.setText(links); // update
                                if (foundLink.contains(searchTerm)) {
                                    String currentKeywords = rightTextArea.getText();
                                    currentKeywords += foundLink + "\n";
                                    rightTextArea.setText(currentKeywords);
                                }
                            }
                        }
                    }

                }

            }
        }
        // mabe do multicatch or something specific to iostream and url exceptions
        catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }


    }

    private void prepareGUI() {
        mainFrame = new JFrame("Webscraper");
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setLayout(new GridLayout(4, 1));

        //end menu at top

        // Create text areas
        searchTermArea = new JTextArea();
        searchTermArea.setText("enter search term");
        searchTermArea.setBounds(50, 25, WIDTH - 100, 75);
        //visibility stuff
        searchTermArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        linkArea = new JTextArea();
        linkArea.setText("input link here");
        linkArea.setBounds(50, 125, WIDTH-100, 75);
        linkArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        mainFrame.add(searchTermArea);
        mainFrame.add(linkArea);
        //exiting logic
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); //set the layout of the pannel

        // Create panel for the two new text areas
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, 10px gap
        
        leftTextArea = new JTextArea();
        leftTextArea.setText("Found links will appear here");
        leftTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        leftTextArea.setEditable(false); // no editing

        rightTextArea = new JTextArea();
        rightTextArea.setText("Found keywords will appear here");
        rightTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rightTextArea.setEditable(false); // same

        //scroll for both
        JScrollPane leftScrollPane = new JScrollPane(leftTextArea);
        JScrollPane rightScrollPane = new JScrollPane(rightTextArea);
        //scrolling, like a panel in a panel
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
        //add text areas to panel
        textAreaPanel.add(leftScrollPane);
        textAreaPanel.add(rightScrollPane);
        //add to main frame
        mainFrame.add(controlPanel);
        mainFrame.add(textAreaPanel);

        mainFrame.setVisible(true);
    }

    private void showEventDemo() {

        JButton startButton = new JButton("Start");
        JButton copyButton = new JButton("Copy");
        JButton stopButton = new JButton("Stop");

        JButton cutepuppyButton = new JButton("Watch cute puppies");

        startButton.setActionCommand("Start");
        copyButton.setActionCommand("Copy");
        stopButton.setActionCommand("Stop");

        cutepuppyButton.setActionCommand("Watch cute puppies");

        startButton.addActionListener(new ButtonClickListener());
        copyButton.addActionListener(new ButtonClickListener());
        stopButton.addActionListener(new ButtonClickListener());
        cutepuppyButton.addActionListener(new ButtonClickListener());

        controlPanel.add(startButton);
        controlPanel.add(copyButton);
        controlPanel.add(stopButton);
        controlPanel.add(cutepuppyButton);

        mainFrame.setVisible(true);
    }

  

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Start")) {
                String input = linkArea.getText();
                String searchInput = searchTermArea.getText();
                searchTerm = searchInput;
                Finder(input);
            } else if (command.equals("Copy")) {
                //https://stackoverflow.com/questions/6710350/copying-text-to-the-clipboard-using-java for help
                StringSelection stringSelection = new StringSelection(rightTextArea.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                System.out.println("Copied to clipboard");
            } else if (command.equals("Watch cute puppies")) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                
            } else if (command.equals("Stop")) {
                System.exit(0);
            } 
        }
    }
} 
