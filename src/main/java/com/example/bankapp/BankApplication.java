package com.example.bankapp;

import com.example.bankapp.config.AppConfig;
import com.example.bankapp.console.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BankApplication {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
            listener.listen();
        }
    }
}

