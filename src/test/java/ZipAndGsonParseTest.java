import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import static org.assertj.core.api.Assertions.assertThat;

public class ZipAndGsonParseTest {

    ClassLoader cl = ZipAndGsonParseTest.class.getClassLoader();

    @Test
    void zipParseTest() throws Exception {
        try (
                InputStream zipFile = cl.getResourceAsStream("example.zip");
                ZipInputStream zis = new ZipInputStream(zipFile)
        ) {
            ZipEntry entry;

            while((entry = zis.getNextEntry())!= null) {
                if (entry.getName().contains(".pdf")) {
                    PDF contentPDF = new PDF(zis);
                    assertThat(contentPDF.text).contains("Simple PDF");
                } else if (entry.getName().contains(".xlsx")) {
                    XLS contentXLS = new XLS(zis);
                    assertThat(contentXLS.excel.getSheetAt(0).getRow(2).getCell(1).getStringCellValue()).contains("fine");
                } else if (entry.getName().contains(".csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> contentCSV = reader.readAll();
                    assertThat(contentCSV.get(2)[1]).contains("Repici");
                }
            }
        }
    }

//    @Test
//    void gsonParseTest() {
//
//    }
}

