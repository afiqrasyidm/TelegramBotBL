package com.mycompany.app;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.api.objects.Update;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class HelloBot extends AbilityBot {
  public static String BOT_TOKEN = "599164260:AAGJcr3NdXBs8TsSng4_b_w_gHwkptD13II";
  public static String BOT_USERNAME = "afiqbotbot";

  public HelloBot() {
    super(BOT_TOKEN, BOT_USERNAME);
  }




  public Ability sayHelloWorld() {
      return Ability
                .builder()
                .name("hello")
                .info("says hello world!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                .build();
  }
  @Override
   public int creatorId() {
     return 470958982;
   }


}
