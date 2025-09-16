package org.klozevitz.services.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.Package;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.klozevitz.annotations.TelegramView;
import org.klozevitz.enities.menu.Category;
import org.klozevitz.enities.users.Department;
import org.klozevitz.enities.users.enums.views.DepartmentView;
import org.klozevitz.entityService.menu.CategoryService;
import org.klozevitz.entityService.users.DepartmentService;
import org.klozevitz.services.util.ExcelToTestParser;
import org.klozevitz.updateProcessor.filters.AbstractTelegramView;
import org.klozevitz.util.DepartmentTelegramView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j
@TelegramView(role = "department", name = DepartmentView.RESOURCE_REQUEST_VIEW)
@RequiredArgsConstructor
public class ResourceRequestView extends AbstractTelegramView {
    @Value("${fileService.service.file_info.url}")
    private String tgFileInfoUrl;
    @Value("${fileService.service.file_storage.url}")
    private String tgFileStorageUrl;
    @Value("${fileService.token}")
    private String token;
    private final ExcelToTestParser parser;
    private final DepartmentService departmentService;
    private final CategoryService categoryService;
    private final DepartmentTelegramView telegramView;


    @Override
    @Transactional
    public ArrayList<BotApiMethod<?>> processUpdate(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasDocument()) {
            return null;
        }

        SendMessage answer;
        var fileId = fileId(update);
        var telegramUserId = telegramUserId(update);
        ResponseEntity<String> response = filePath(fileId);

        if (response.getStatusCode() == HttpStatus.OK) {
            ByteArrayInputStream inputStream = fileAsByteArrayInputStream(response);
            try {
                var workbook = new XSSFWorkbook(Package.open(inputStream));
                Set<Category> menu = parser.parseFile(workbook);

                var currentDepartment = departmentService.findByTelegramUserId(telegramUserId);
                long departmentId = currentDepartment.getId();

                setLinks(currentDepartment, menu);
                removeExisting(departmentId, menu);
                categoryService.saveAll(menu);

                answer = parsedFileNotification(update, new ArrayList<>(menu));
            } catch (IOException | InvalidFormatException e) {
                answer = parseErrorNotification(update);
            }
        } else {
            answer = uploadFailNotification(update);
        }

        return answerAsList(answer);
    }

    private void removeExisting(long departmentId, Set<Category> menu) {
        menu.forEach(
                category ->
                        categoryService
                                .removeByDepartmentId(
                                        departmentId,
                                        category.getName()
                                )
        );
    }

    private void setLinks(Department currentDepartment, Set<Category> menu) {
        menu.forEach(category -> category.setDepartment(currentDepartment));
    }

    private ByteArrayInputStream fileAsByteArrayInputStream(ResponseEntity<String> response) {
        assert response.getBody() != null;
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(
                jsonObject
                        .getJSONObject("result")
                        .getString("file_path")
        );
        byte[] fileAsByteArray = downloadFile(filePath);
        return new ByteArrayInputStream(fileAsByteArray);
    }

    private byte[] downloadFile(String filePath) {
        var fileUrl = tgFileStorageUrl
                .replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObject;
        try {
            urlObject = new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try (InputStream inputStream = urlObject.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<String> filePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                tgFileInfoUrl,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }

    private String fileId(Update update) {
        return update
                .getMessage()
                .getDocument()
                .getFileId();
    }

    private SendMessage parsedFileNotification(Update update, List<Category> menu) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.RESOURCES_MANAGEMENT_VIEW);

        return telegramView.parsedFileNotification(update, menu);
    }

    private SendMessage parseErrorNotification(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);

        return telegramView.parseErrorNotification(update);
    }

    private SendMessage uploadFailNotification(Update update) {
        var telegramUserId = telegramUserId(update);
        departmentService.setCurrentView(telegramUserId, DepartmentView.WELCOME_VIEW);

        return telegramView.uploadFailNotification(update);
    }
}
