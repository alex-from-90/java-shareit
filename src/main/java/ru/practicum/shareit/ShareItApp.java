package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {
    /*С таким набором информации по учёбе - это тихий ужас... Не смог сделать настройки postgresql
    Не уверен что правильно понял тему... Вообще, насколько понимаю, в чистом виде такое редко используется
    Просьба сильно не мучать, если конечно всё не совсем плохо)). */
    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }
}
