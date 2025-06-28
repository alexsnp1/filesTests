import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestsWithFiles {
    private ClassLoader cl = TestsWithFiles.class.getClassLoader();

    @Test
    void zipFileTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("file.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null ) {
                System.out.println(entry.getName());
            }
        }
    }
}