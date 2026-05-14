import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadFX {
    public static void main(String[] args) throws Exception {
        String[] jarUrls = {
                "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/25/javafx-controls-25-win.jar",
                "https://repo1.maven.org/maven2/org/openjfx/javafx-base/25/javafx-base-25-win.jar",
                "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/25/javafx-graphics-25-win.jar",
                "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/25/javafx-fxml-25-win.jar"
        };

        File libDir = new File("lib");
        libDir.mkdirs();

        System.out.println("Downloading JavaFX 25 JARs from Maven Central...\n");

        for (String jarUrl : jarUrls) {
            String jarName = jarUrl.substring(jarUrl.lastIndexOf('/') + 1);
            System.out.print("Downloading " + jarName + "... ");

            try {
                URL url = new URL(jarUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);
                conn.addRequestProperty("User-Agent", "Mozilla/5.0");

                int code = conn.getResponseCode();
                if (code == 200 || code == 301 || code == 302) {
                    try (InputStream in = conn.getInputStream();
                            FileOutputStream out = new FileOutputStream(new File(libDir, jarName))) {
                        byte[] buffer = new byte[8192];
                        int read;
                        long total = 0;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                            total += read;
                        }
                        System.out.println("✓ (" + (total / 1024 / 1024) + " MB)");
                    }
                } else {
                    System.out.println("✗ (HTTP " + code + ")");
                }
            } catch (Exception e) {
                System.out.println("✗ (" + e.getMessage() + ")");
            }
        }

        System.out.println("\n✓ Download complete! Check lib/ folder for JARs");
    }
}
