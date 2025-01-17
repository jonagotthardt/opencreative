import ua.mcchickenstudio.opencreative.OpenCreative;
import ua.mcchickenstudio.opencreative.settings.Sounds;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * This Main class is called when user executes .jar file.
 * It's not called when plugin loads on PaperMC server.
 * @see OpenCreative
 */
public class Main {

    /**
     * Creates window with installation instruction when user executes this .jar file.
     * @param args Arguments to launch Java application
     */
    public static void main(String[] args) {
        String message = "This is OpenCreative+, a plugin for Minecraft servers.\nMade by McChicken Studio 2025. \n \nInstallation:\n To install plugin please download PaperMC server, then load it and accept EULA.\n After that put this .jar file into server's /plugins/ folder and launch a server.\n \nPress OK to close.";
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(new JFrame(), message, "OpenCreative+", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

}
