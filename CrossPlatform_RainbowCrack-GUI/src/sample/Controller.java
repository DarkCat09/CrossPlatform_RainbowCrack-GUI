package sample;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.zip.*;

public class Controller {
    @FXML public MenuBar menuBar1;
    @FXML public Menu menuFile;
    @FXML public MenuItem menuItemClose;
    @FXML public Menu menuHelp;
    @FXML public MenuItem menuItemAboutProgram;
    @FXML public MenuItem menuItemAboutUs;
    @FXML public GridPane gridPane1;
    @FXML public BorderPane borderPane1;
    @FXML public TextField textField1;
    @FXML public TextArea textArea1;
    @FXML public Button button1;
    @FXML public Button button2;
    @FXML public Button button3;
    @FXML public MenuItem menuItemFreeRainbowTables1;
    @FXML public MenuItem menuItemConvertRtiToRto1;
    @FXML public TextArea outputTextArea1;
    @FXML public Stage primaryStage;

    public String tablesDir = "";
    public File hashesFile;
    public ArrayList<String> hashes = new ArrayList<>();

    public void MenuItemClose_Click(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void MenuItemAboutUs_Click(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Help \"About Us\" is not ready yet.",
                                ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Information");
        alert.showAndWait();
    }

    public void ChooseTablesDirectory(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select Directory with Rainbow Tables");
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = dc.showDialog(primaryStage);

        if (dir != null) {
            tablesDir = dir.getAbsolutePath();
            textField1.setText(tablesDir);
        }
        else {
            textField1.setText("");
        }
    }

    public void ChooseHashFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select File with Hashes");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text File", "*.txt")
        );

        File f = fc.showOpenDialog(primaryStage);
        if (f != null) {
            hashesFile = f;

            try {
                int c;
                FileReader reader = new FileReader(hashesFile);

                String hash = "";
                while ((c = reader.read()) != -1) {
                    if ((char)c != '\n') {
                        hash += (char)c;
                    }
                    else {
                        hashes.add(hash);
                        hash = "";
                    }
                }

                for (String h: hashes) {
                    textArea1.appendText(h + "\n");
                }
            }
            catch (FileNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot read file with hashes.\n" +
                                        "File Not Found.", ButtonType.OK);
                alert.setTitle("Error - FileNotFoundException");
                alert.setHeaderText("An I/O Error has occurred!");
                alert.showAndWait();
            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Cannot read file with hashes.\n" +
                                        ex, ButtonType.OK);
                alert.setTitle("Error - IOException");
                alert.setHeaderText("An I/O Error has occurred!");
                alert.showAndWait();
            }
        }
    }

    public void MenuItemFreeRainbowTables_Click(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you need some free rainbow tables?\n" +
                                "Download from site https://freerainbowtables.com/",
                                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText("Download Rainbow Tables");
        alert.setTitle("Free Rainbow Tables!");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            try {
                Desktop.getDesktop().browse(new URI("https://freerainbowtables.com/"));
            }
            catch (Exception ex) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR, String.valueOf(ex), ButtonType.OK);
                alert1.setTitle("Error - Exception");
                alert1.setHeaderText("An error has occurred!");
                alert1.showAndWait();
            }
        }
    }

    public void RtiConverting(ActionEvent actionEvent) throws IOException {
        BorderPane rtiwindow = FXMLLoader.load(getClass().getResource("rticonv.fxml"));
        Scene rticonvScene = new Scene(rtiwindow, 450, 160);
        Stage newWindow = new Stage();

        newWindow.setTitle("Converting RTI 2 RTO");
        newWindow.setScene(rticonvScene);

        newWindow.show();
    }

    public void StartCracking(ActionEvent actionEvent) {
        String osname  = SysInfo.GetOperatingSystem();
        String osver   = SysInfo.GetSystemVersion();
        String osarch  = System.getProperty("os.arch") + " or x" + SysInfo.GetSystemArchitecture();
        String filesep = System.getProperty("file.separator");
        String linesep = System.getProperty("line.separator");

        //for debug
        Alert osAlert = new Alert(Alert.AlertType.INFORMATION, "All about your system:\n\n" +
                        osname + " ver. " + osver + ", architecture - " + osarch + "\n" +
                        "file separator - \"" + filesep + "\", line separator in text files - \"" + linesep + "\"",
                        ButtonType.OK);
        osAlert.setTitle("Debugging");
        osAlert.setHeaderText("Your system");
        osAlert.showAndWait();

        try {

            String rcrackArchiveFilename =
                    (SysInfo.GetOperatingSystem() == "windows") && (SysInfo.GetSystemArchitecture() == 64) ?
                        "rainbowcrack-1.7-win64.zip" :
                    (SysInfo.GetOperatingSystem() == "windows") && (SysInfo.GetSystemArchitecture() == 32) ?
                        "rainbowcrack-1.6.1-win32.zip" :
                    ((SysInfo.GetOperatingSystem() == "linux") || (SysInfo.GetOperatingSystem() == "unix"))
                    && (SysInfo.GetSystemArchitecture() == 64) ?
                        "rainbowcrack-1.7-linux64.zip" :
                    ((SysInfo.GetOperatingSystem() == "linux") || (SysInfo.GetOperatingSystem() == "unix"))
                    && (SysInfo.GetSystemArchitecture() == 32) ?
                        "rainbowcrack-1.6.1-linux32.zip" :
                    (SysInfo.GetOperatingSystem() == "mac") ?
                        null : null;

            ZipInputStream zin = null;
            if (rcrackArchiveFilename != null) {
                System.out.println(getClass().getResource("/resources/" + rcrackArchiveFilename));
                System.out.println(
                        getClass().getResourceAsStream(
                                "/resources/" + rcrackArchiveFilename));

                //extracting zip from jar
                InputStream jar_is = getClass().getResourceAsStream(
                        "/resources/" + rcrackArchiveFilename);
                OutputStream zip_from_jar = new FileOutputStream(rcrackArchiveFilename);
                byte[] buf = new byte[1024];
                int len;
                while ((len = jar_is.read(buf)) > 0) {
                    zip_from_jar.write(buf, 0, len);
                }
                jar_is.close();
                zip_from_jar.close();

                zin = new ZipInputStream(new FileInputStream(rcrackArchiveFilename));
            }

            if (zin != null) {
                ZipEntry zen;
                String filename;
                long filesize;

                String newPath = rcrackArchiveFilename.substring(
                        0, rcrackArchiveFilename.length() - 4);
                new File(newPath).mkdir();

                while ((zen = zin.getNextEntry()) != null) {

                    filename = zen.getName();
                    filesize = zen.getSize();

                    if (!zen.isDirectory()) {

                        System.out.println("Unpacking " + filename + ", size: " + filesize + "b");

                        File parentDest = new File(filename).getParentFile();
                        parentDest.mkdirs();
                        FileOutputStream fout = new FileOutputStream(filename);
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }
                        fout.flush();
                        zin.closeEntry();
                        fout.close();
                    }
                }
                zin.close();

                System.out.println("Done!");
                System.out.println();

                //reading rcrack
                ProcessBuilder pb = new ProcessBuilder(
                        rcrackArchiveFilename.substring(0, rcrackArchiveFilename.length() - 4) +
                                  "rcrack.exe");
                Process p = pb.start();
                InputStreamReader rcrack_ins = new InputStreamReader(p.getInputStream());
                BufferedReader rcrack_br = new BufferedReader(rcrack_ins);
                String line = "";
                while ((line = rcrack_br.readLine()) != null) {
                    //code
                }

            }
            else {
                System.out.println("RainbowCrack does not supported in your OS.");
            }
        }
        catch (IOException | NullPointerException ex) {
            System.out.println(ex);
            Alert alert = new Alert(Alert.AlertType.ERROR, String.valueOf(ex), ButtonType.OK);
            alert.setTitle("Error - IOException");
            alert.setHeaderText("An I/O Error has occurred!");
            alert.showAndWait();
        }
    }
}
