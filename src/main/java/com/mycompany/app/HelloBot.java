package com.mycompany.app;

import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.api.objects.Update;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import com.mycompany.app.models.*;

public class HelloBot extends BaseBot {

  public HelloBot() {
    super();
  }

  public Ability sayHelloWorld() {
    return Ability.builder().name("hello").info("says hello world!").locality(ALL).privacy(PUBLIC)
        .action(ctx -> {
          // open connection to db
          openDBConnection();
          // create new user
          new User().set("username","@vgeraldo").saveIt();
          // close connection
          closeDBConnection();

          silent.send("Hello world!", ctx.chatId());
        }).build();
  }
}
