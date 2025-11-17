package org.example.studio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateDto {

    @NotBlank(message = "Текст поста обязателен")
    @Size(min = 1, max = 5000, message = "Текст поста не должен превышать 5000 символов")
    private String content;

    private MultipartFile attachment;
}