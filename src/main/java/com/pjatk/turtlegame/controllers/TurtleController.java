package com.pjatk.turtlegame.controllers;

import com.pjatk.turtlegame.repositories.TurtleRepository;
import com.pjatk.turtlegame.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/turtles")
@AllArgsConstructor
public class TurtleController {
    TurtleRepository turtleRepository;
    UserService userService;


}
