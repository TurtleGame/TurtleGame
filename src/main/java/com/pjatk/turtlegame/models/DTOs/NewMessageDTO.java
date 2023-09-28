package com.pjatk.turtlegame.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NewMessageDTO {

    private String recipient;
    private String title;
    private String content;
    private Integer gold;

}
