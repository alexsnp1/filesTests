import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestsWithFiles {
    private ClassLoader cl = TestsWithFiles.class.getClassLoader();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void pdfInsideZipTest() throws Exception {
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
            }
        }
    }
    @Test
    void xlsInsideZipTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("file.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
                if (entry.getName().equals("person.xls")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(2).getCell(2).getStringCellValue();
                    Assertions.assertTrue(actualValue.contains("Hashimoto"));

                }
            }
        }
    }
    @Test
    void csvInsideZipTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("file.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
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
    @Test
    void jsonFileTest() throws Exception {
        try (InputStream input = cl.getResourceAsStream("IPhone.json")) {
            JsonNode actual = objectMapper.readTree(input);
            Assertions.assertEquals("IPhone 16 Pro Max", actual.get("model").asText());
            Assertions.assertEquals(1200, actual.get("price").asInt());
            Assertions.assertEquals(true, actual.get("MagSafe").asBoolean());

            List<String> expectedResistance = List.of("splash", "water", "dust");
            List<String> actualResistance = new ArrayList<>();
            for (JsonNode res : actual.get("Resistance")) {
                actualResistance.add(res.asText());
            }
            Assertions.assertEquals(expectedResistance, actualResistance);

        }
    }
}