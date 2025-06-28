import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestsWithFiles {
    private ClassLoader cl = TestsWithFiles.class.getClassLoader();

    @Test
    void zipFileTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("file.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
                if (entry.getName().equals("sample.pdf")) {
                    PDF pdf = new PDF(zis);
                    Assertions.assertTrue(pdf.text.contains("This PDF is three pages long"));
                    Assertions.assertEquals("Philip Hutchison", pdf.author);
                }
                if (entry.getName().equals("person.xls")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(2).getCell(2).getStringCellValue();
                    Assertions.assertTrue(actualValue.contains("Hashimoto"));

                }
                }
            }
        }
    }