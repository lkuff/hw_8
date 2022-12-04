import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import model.EmployeeJson;

import static org.assertj.core.api.Assertions.assertThat;


public class ZipAndGsonParseTest {

    ClassLoader cl = ZipAndGsonParseTest.class.getClassLoader();

    @Test
    void zipParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("files/example.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    PDF contentPDF = new PDF(zis);
                    assertThat(contentPDF.text).contains("A Simple PDF");
                } else if (entry.getName().contains(".xlsx")) {
                    XLS contentXLS = new XLS(zis);
                    assertThat(contentXLS.excel.getSheetAt(0).getRow(1).getCell(2).getStringCellValue()).contains("fine");
                } else if (entry.getName().contains(".csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> contentCSV = reader.readAll();
                    assertThat(contentCSV.get(2)[1]).contains("Repici");
                }
            }
        }
    }

    @Test
    void jsonParseTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try (
                InputStream resource = cl.getResourceAsStream("files/employee.json")
        ) {
            EmployeeJson employeeJson = objectMapper.readValue(resource, EmployeeJson.class);
            assertThat(employeeJson.name).isEqualTo("Aleix Melon");
            assertThat(employeeJson.id).isEqualTo("E00245");
            assertThat(employeeJson.age).isEqualTo(23);
            assertThat(employeeJson.married).isTrue();
            assertThat(employeeJson.address.street).isEqualTo("32, Laham St.");
            assertThat(employeeJson.address.city).isEqualTo("Innsbruck");
            assertThat(employeeJson.address.country).isEqualTo("Austria");
        }
    }
}