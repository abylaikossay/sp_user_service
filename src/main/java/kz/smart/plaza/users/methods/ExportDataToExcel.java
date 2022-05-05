package kz.smart.plaza.users.methods;

import kz.smart.plaza.users.models.responses.TagResponse;
import kz.smart.plaza.users.models.responses.UserResponse;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExportDataToExcel {


    public String Export(List<UserResponse> userResponses, int count) throws SQLException, IOException, NoSuchMethodException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        AtomicReference<XSSFCell> cell = new AtomicReference<>();
        AtomicReference<XSSFRow> row = new AtomicReference<>();

        XSSFSheet spreadsheet = workbook.createSheet("users");

        AtomicInteger rowNum = new AtomicInteger();
        AtomicInteger colNum = new AtomicInteger();


        row.set(spreadsheet.createRow(rowNum.getAndIncrement()));

        cell.set(row.get().createCell(0));
        cell.get().setCellValue("ID");

        cell.set(row.get().createCell(1));
        cell.get().setCellValue("Имя");

        cell.set(row.get().createCell(2));
        cell.get().setCellValue("Пол");

        cell.set(row.get().createCell(3));
        cell.get().setCellValue("Дата рождения");

        cell.set(row.get().createCell(4));
        cell.get().setCellValue("Платформа");

        cell.set(row.get().createCell(5));
        cell.get().setCellValue("Телефон");

        cell.set(row.get().createCell(6));
        cell.get().setCellValue("Email");

        cell.set(row.get().createCell(7));
        cell.get().setCellValue("Город");

        cell.set(row.get().createCell(8));
        cell.get().setCellValue("Возраст");

        cell.set(row.get().createCell(9));
        cell.get().setCellValue("Бонусы");

        cell.set(row.get().createCell(10));
        cell.get().setCellValue("Активные бонусы");

        cell.set(row.get().createCell(11));
        cell.get().setCellValue("Тэги");

        cell.set(row.get().createCell(12));
        cell.get().setCellValue("Последняя активность");


        userResponses.forEach(userResponse -> {
                    colNum.set(0);
                    row.set(spreadsheet.createRow(rowNum.getAndIncrement()));

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getId());
//
                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getSurname() + " " + userResponse.getName());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    String gender = "Пол не выбран";
                    if (userResponse.getGender()!= null) {
                        if (userResponse.getGender().equals(1)) {
                            gender = "Мужской";
                        } else if (userResponse.getGender().equals(0)){
                            gender = "Женский";
                        }
                    }
                    cell.get().setCellValue(gender);

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    if(userResponse.getBirthDate() != null){
                        cell.get().setCellValue(userResponse.getBirthDate().toString());
                    }

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getPlatform());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getPhone());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getEmail());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    String city = "Город не выбран";
                    if (userResponse.getCityId() != null) {
                        if (userResponse.getCityId().equals(2)){
                            city = "Алматы";
                        } else if (userResponse.getCityId().equals(3)) {
                            city = "Шымкент";
                        }
                    }
                    cell.get().setCellValue(city);

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    if(userResponse.getAge() != null){
                        cell.get().setCellValue(userResponse.getAge());
                    }

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getBonuses());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    cell.get().setCellValue(userResponse.getActiveBonuses());

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    String tags = "";
                    List<String> tagsList =  new ArrayList<>();
                            userResponse.getTags().forEach( element -> {
                                tagsList.add(element.getName());
                    });
                            tags = String.join(",", tagsList);
                    cell.get().setCellValue(tags);

                    cell.set(row.get().createCell(colNum.getAndIncrement()));
                    if(userResponse.getLastLogin() != null) {
                        cell.get().setCellValue(userResponse.getLastLogin().toString());
                    }
         }
        );
            for (int i = 0; i < 13; i++) {
                spreadsheet.autoSizeColumn(i);
            }



        File exile = new File("var\\tmp\\excels\\users-" + new Date().getTime() +".xlsx");
        FileOutputStream out = new FileOutputStream(exile);
        workbook.write(out);
        out.close();

        return exile.getCanonicalPath();
    }
}

