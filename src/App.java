import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * App
 *
 * @author ranggarmaste
 * @since Mar 27, 2018.
 */
public class App {

    private JButton encryptButton;
    private JRadioButton RSARadioButton;
    private JButton uploadFileButton;
    private JButton decryptButton;
    private JRadioButton ECCEGRadioButton;
    private JButton uploadKeyButton;
    private JRadioButton longRadioButton;
    private JRadioButton bigIntRadioButton;
    private JRadioButton longLongIntRadioButton;
    private JPanel root;
    private JButton saveButton;
    private JLabel filenameLabel;
    private JLabel keynameLabel;
    private JButton generateKeyButton;
    private JTextField nTextField;
    private JLabel sizeLabel;
    private JLabel timeLabel;
    private JTextField keyTextField;
    private JTextField keyfileTextField;
    private JRadioButton keyUploadRadioButton;
    private JRadioButton manualRadioButton;
    private JLabel statusLabel;
    private JLabel outputLabel;
    private JLabel inputLabel;

    private String filePath;
    private String keyPath;
    private String type = RSA.LONG_TYPE;
    private String algorithm = "RSA";
    private boolean manual = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setSize(800, 600);
        frame.setContentPane(new App(frame).root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public App(JFrame frame) {
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(longRadioButton);
        typeGroup.add(bigIntRadioButton);
        typeGroup.add(longLongIntRadioButton);
        longRadioButton.addActionListener(e -> {
            type = RSA.LONG_TYPE;
        });
        bigIntRadioButton.addActionListener(e -> {
            type = RSA.BIGINTEGER_TYPE;
        });
        longLongIntRadioButton.addActionListener(e -> {
            type = RSA.LONGLONGINT_TYPE;
        });
        longRadioButton.setSelected(true);

        ButtonGroup algoGroup = new ButtonGroup();
        algoGroup.add(RSARadioButton);
        algoGroup.add(ECCEGRadioButton);
        RSARadioButton.addActionListener(e -> {
            algorithm = "RSA";
        });
        ECCEGRadioButton.addActionListener(e -> {
            algorithm = "ECCEG";
        });
        RSARadioButton.setSelected(true);

        ButtonGroup manualGroup = new ButtonGroup();
        manualGroup.add(manualRadioButton);
        manualGroup.add(keyUploadRadioButton);
        manualRadioButton.addActionListener(e -> {
            manual = true;
        });
        keyUploadRadioButton.addActionListener(e -> {
            manual = false;
        });
        keyUploadRadioButton.setSelected(true);

        uploadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                filePath = file.getAbsolutePath();
                filenameLabel.setText(file.getName());

                byte[] bytes = Utils.readFile(filePath);
                StringBuilder builder =new StringBuilder();
                for (byte b : bytes) {
                    builder.append((char) b);
                }
                if (builder.length() > 300) {
                    builder.delete(300, builder.length());
                    builder.append(" ...");
                }
                inputLabel.setText(String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 200, builder.toString()));
                inputLabel.setHorizontalAlignment(JLabel.LEFT);
            }
        });

        uploadKeyButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                keyPath = file.getAbsolutePath();
                keynameLabel.setText(file.getName());
            }
        });

        generateKeyButton.addActionListener(e -> {
            String filename = keyfileTextField.getText();
            if (algorithm.equals("RSA")) {
                RSA rsa = new RSA();
                if (type.equals(RSA.LONG_TYPE)) {
                    rsa.createKey(filename, 179, 8191);
                } else if (type.equals(RSA.BIGINTEGER_TYPE)) {
                    rsa.createKey(filename, new BigInteger("2147483647"), new BigInteger("67280421310721"));
                } else {
                    rsa.createKey(filename, new LongLongInteger("2147483647"), new LongLongInteger("67280421310721"));
                }
            } else {

            }
            JOptionPane.showMessageDialog(frame, String.format("We have successfully created " + filename + ".pub and " + filename + ".pri"));
        });

        encryptButton.addActionListener(event -> {
            String keyfile = getKeyFile();
            if (algorithm.equals("RSA")) {
                RSA rsa = new RSA();
                long tStart = System.currentTimeMillis();
                statusLabel.setText("Status: Processing");
                rsa.encrypt(filePath, "temp.out", keyfile, type);
                statusLabel.setText("Status: Done");
                long tEnd = System.currentTimeMillis();
                double elapsed = (tEnd - tStart) / 1000.0;
                timeLabel.setText(Double.toString(elapsed));
                showEncryptStats();
            } else {

            }
        });

        decryptButton.addActionListener(event -> {
            String keyfile = getKeyFile();
            if (algorithm.equals("RSA")) {
                RSA rsa = new RSA();
                long tStart = System.currentTimeMillis();
                statusLabel.setText("Status: Processing");
                rsa.decrypt(filePath, "temp.out", keyfile, type);
                statusLabel.setText("Status: Done");
                long tEnd = System.currentTimeMillis();
                double elapsed = (tEnd - tStart) / 1000.0;
                timeLabel.setText(Double.toString(elapsed));
                showDecryptStats();
            } else {

            }
        });

        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String saveLocation = fileChooser.getSelectedFile().getAbsolutePath();
                byte[] bytes = Utils.readFile("temp.out");
                Path path = Paths.get(saveLocation);
                try {
                    Files.write(path, bytes);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
    }

    public String getKeyFile() {
        String keyfile = keyPath;
        if (manual) {
            String e = keyTextField.getText();
            String n = nTextField.getText();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("temp.pub"));
                writer.write(n + " " + e + "\n");
                writer.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            keyfile = "temp.pub";
        }
        return keyfile;
    }

    public void showEncryptStats() {
        byte[] bytes = Utils.readFile("temp.out");
        String hex = Utils.toHexString(bytes);
        if (hex.length() > 300) {
            hex = hex.substring(0, 300) + " ...";
        }
        outputLabel.setText(String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 200, hex));
        outputLabel.setHorizontalAlignment(JLabel.LEFT);
        sizeLabel.setText(Integer.toString(bytes.length));
    }

    public void showDecryptStats() {
        byte[] bytes = Utils.readFile("temp.out");
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append((char) b);
        }
        if (builder.length() > 300) {
            builder.delete(300, builder.length());
            builder.append(" ...");
        }
        outputLabel.setText(String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 200, builder.toString()));
        outputLabel.setHorizontalAlignment(JLabel.LEFT);
        sizeLabel.setText(Integer.toString(bytes.length));
    }
}
