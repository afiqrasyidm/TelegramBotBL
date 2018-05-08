
package com.mycompany.app;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Igor Polevoy on 11/16/15.
 */
public class Tables {

    @Table("public.user")
    public static class User extends Model {}
    public static final User USER = new User();

    @Table("public.history")
    public static class History extends Model {}
    public static final History HISTORY = new History();
}