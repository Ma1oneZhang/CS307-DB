package edu.sustech.cs307;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class DBEntry {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello, This is CS307-DB!");
        Scanner scanner = new Scanner(System.in);
        String sql = "";
        boolean running = true;
        while (running) {
            try {
                sql = scanner.nextLine();
                if (sql.equalsIgnoreCase("exit")) {
                    running = false;
                    continue;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.err.println("An error occurred. Exiting....");
            }
            JSqlParser parser = new CCJSqlParserManager();
            try {
                Statement stmt = parser.parse(new StringReader(sql));

                System.out.println(stmt);
            } catch (JSQLParserException e) {
                System.err.println(e.getMessage());
                System.err.println("The SQL Statement is not valid. Please try again.");
            }
        }

    }
}