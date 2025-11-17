package org.example.studio.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudioUpdateDto {

    @Size(min = 2, max = 50, message = "Имя студии должно быть от 2 до 50 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    private MultipartFile avatar;
}