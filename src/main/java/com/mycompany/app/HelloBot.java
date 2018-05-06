package com.mycompany.app;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.api.objects.Update;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import io.github.cdimascio.dotenv.Dotenv;

public class HelloBot extends AbilityBot {
  private static Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMalformed().ignoreIfMissing().load();

  public HelloBot() {
    super(validToken(), validUsername());
  }

  public Ability sayHelloWorld() {
    return Ability.builder().name("hello").info("says hello world!").locality(ALL).privacy(PUBLIC)
        .action(ctx -> silent.send("Hello world!", ctx.chatId())).build();
  }

  private static String validToken() {
    String botToken = System.getenv().get("BOT_TOKEN");
    if (botToken == null) {
      return dotenv.get("BOT_TOKEN");
    }
    return botToken;
  }

  private static String validUsername() {
    String botUsername = System.getenv().get("BOT_USERNAME");
    if (botUsername == null) {
      return dotenv.get("BOT_USERNAME");
    }
    return botUsername;
  }

  @Override
  public int creatorId() {
    String creatorId = System.getenv().get("CREATOR_ID");
    if(creatorId == null){
      return Integer.parseInt(dotenv.get("CREATOR_ID"));
    }
    return Integer.parseInt(creatorId);
  }
}
