import javax.swing.*;
import java.awt.*;

public class Main {

    /**
     * Creates window with installation instruction when user executes this .jar file.
     * @param args
     */
    public static void main(String[] args) {
        String message = "This is OpenCreative+, a plugin for Minecraft servers.\nMade by McChicken Studio 2024. \n \nInstallation:\n To install plugin please download PaperMC 1.16.5 server, then load it and accept EULA.\n After that put this .jar file into server's /plugins/ folder and launch a server.\n \nPress OK to close.";
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(new JFrame(), message, "OpenCreative+", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

}
