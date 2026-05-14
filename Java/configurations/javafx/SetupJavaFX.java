import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SetupJavaFX {
    public static void main(String[] args) throws Exception {
        // Try multiple download mirrors
        String[] urls = {
                "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/25/javafx-controls-25-win.jar",
                "https://gluonhq.com/download/javafx-sdk-25_windows-x64_bin.zip"
        };

        System.out.println("========================================");
        System.out.println("JavaFX 25 Setup for Java 25.0.3");
        System.out.println("========================================\n");

        // Check if already extracted
        File sdkDir = new File("javafx-25-sdk");
        if (sdkDir.exists() && new File(sdkDir, "lib").exists()) {
            System.out.println("✓ JavaFX SDK already installed!");
            System.out.println("\nYou can now:");
            System.out
                    .println("1. Use: javac --module-path javafx-25-sdk\\lib --add-modules javafx.controls Main.java");
            System.out.println("2. Run: java --module-path javafx-25-sdk\\lib --add-modules javafx.controls Main");
            return;
        }

        System.out.println("JavaFX SDK not found locally.\n");
        System.out.println("Attempting to download from Maven Central Repository...\n");

        String libDir = "javafx-25-sdk/lib";
        new File(libDir).mkdirs();

        // Download the JARs individually from Maven Central
        String[] modules = {
                "javafx-controls-25-win",
                "javafx-fxml-25-win",
                "javafx-graphics-25-win",
                "javafx-base-25-win"
        };

        boolean allSuccess = true;
        for (String module : modules) {
            String jarUrl = "https://repo1.maven.org/maven2/org/openjfx/" + module + "/" + module + ".jar";
            String jarFile = libDir + File.separator + module + ".jar";

            System.out.println("Downloading " + module + ".jar...");
            if (downloadFile(jarUrl, jarFile)) {
                System.out.println("✓ " + module + ".jar downloaded");
            } else {
                System.out.println("✗ Failed to download " + module + ".jar");
                allSuccess = false;
            }
        }

        if (allSuccess) {
            System.out.println("\n✓ JavaFX 25 setup complete!");
            System.out.println("\nYou can now compile and run your JavaFX application.");
        } else {
            System.out.println("\n✗ Some files failed to download.");
            System.out.println("\nAlternatively, download manually from:");
            System.out.println("https://gluonhq.com/download/javafx-25/");
        }
    }

    static boolean downloadFile(String urlStr, String destFile) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream in = conn.getInputStream();
                        FileOutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            // Silent fail, try next
        }
        return false;
    }
}
