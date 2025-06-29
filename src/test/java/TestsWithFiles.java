import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
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
                if (entry.getName().equals("username.csv")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }

                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                    try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(bais))
                            .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                            .build())
                    {
                        List<String[]> data = csvReader.readAll()
                                .stream()
                                .filter(row -> row.length > 0 && !row[0].trim().isEmpty())
                                .map(row -> Arrays.stream(row).map(String::trim).toArray(String[]::new))
                                .toList();
                        Assertions.assertEquals(6, data.size());
                        Assertions.assertArrayEquals(
                                new String[] {"Username" , "Identifier", "First name", "Last name"},
                                data.get(0)
                        );
                        Assertions.assertArrayEquals(
                                new String[] {"booker12" , "9012", "Rachel", "Booker"},
                                data.get(1)
                        );
                    }
                }
            }
        }
    }
}