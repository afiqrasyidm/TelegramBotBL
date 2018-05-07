package com.mycompany.app;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )throws Exception 
    {
       ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
        botsApi.registerBot(new HelloBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
