package com.example.demo.controller;

import com.example.demo.entities.User;
import com.example.demo.payload.UserDto;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserRegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {

    private UserRegistrationService userRegistrationService;
    private final UserRepository userRepository;

    public UserRegistrationController(UserRegistrationService userRegistrationService, UserRepository userRepository) {
        this.userRegistrationService = userRegistrationService;
        this.userRepository = userRepository;
    }
    //http://localhost:8080//api/users/photo/register
    @PostMapping("/photo/register")
    public ResponseEntity<String> registerUser(@RequestParam("photo") MultipartFile photo,
                                               @RequestParam("userDto") String userDtoJson) throws IOException {
        UserDto userDto = new ObjectMapper().readValue(userDtoJson, UserDto.class);

        if (!photo.isEmpty()) {
            userDto.setPhoto(photo);
        }

        UserDto registeredUser = userRegistrationService.registerUser(userDto);
        return ResponseEntity.ok("User registration successful");
    }
    // http://localhost:8080/api/users/download/excel
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadUsersExcel() {
        List<User> users = userRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("First Name");
            headerRow.createCell(2).setCellValue("Last Name");
            headerRow.createCell(3).setCellValue("City");
            headerRow.createCell(4).setCellValue("Email");
            headerRow.createCell(5).setCellValue("Mobile");
            headerRow.createCell(6).setCellValue("State");
            headerRow.createCell(7).setCellValue("Country");
            headerRow.createCell(8).setCellValue("Pin Code");
            headerRow.createCell(8).setCellValue("password");
            headerRow.createCell(8).setCellValue("user Photo");

            // Populate data rows
            int rowNum = 1;
            for (User user : users) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(user.getId());
                dataRow.createCell(1).setCellValue(user.getFirstName());
                dataRow.createCell(2).setCellValue(user.getLastName());
                dataRow.createCell(3).setCellValue(user.getCity());
                dataRow.createCell(4).setCellValue(user.getEmail());
                dataRow.createCell(5).setCellValue(user.getMobile());
                dataRow.createCell(6).setCellValue(user.getState());
                dataRow.createCell(7).setCellValue(user.getCountry());
                dataRow.createCell(8).setCellValue(user.getPinCode());
                dataRow.createCell(8).setCellValue(user.getPassword());
                dataRow.createCell(8).setCellValue(user.getUserPhoto());
            }

            // Auto-size columns
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            // Set the appropriate headers for Excel response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "users.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}