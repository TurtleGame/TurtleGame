package com.pjatk.turtlegame.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class banFormDTO {

    private String playerUsername;
    private String reason;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate banExpireAt;

}
