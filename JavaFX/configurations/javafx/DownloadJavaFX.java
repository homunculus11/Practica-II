import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadJavaFX {
    public static void main(String[] args) throws Exception {
        String downloadUrl = "https://gluonhq.com/download/javafx-sdk-25_windows-x64_bin.zip";
        String zipFile = "javafx-25-sdk.zip";
        String extractDir = ".";

        System.out.println("Downloading JavaFX 25 SDK...");
        System.out.println("URL: " + downloadUrl);

        try {
            URL url = new URL(downloadUrl);
            try (InputStream in = url.openStream();
                    FileOutputStream out = new FileOutputStream(zipFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                    System.out.print("\rDownloaded: " + (totalBytes / 1024 / 1024) + " MB");
                }
            }
            System.out.println("\n✓ Download complete!");

            System.out.println("Extracting JavaFX SDK...");
            extractZip(zipFile, extractDir);
            System.out.println("✓ Extraction complete!");
            System.out.println("✓ JavaFX 25 is ready to use!");

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void extractZip(String zipFile, String destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String filePath = destDir + File.separator + entry.getName();

                if (entry.isDirectory()) {
                    new File(filePath).mkdirs();
                } else {
                    new File(filePath).getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        new File(zipFile).delete();
    }
}
