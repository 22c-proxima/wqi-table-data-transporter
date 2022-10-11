package com.world_quant.test;

import java.sql.SQLException;
import lombok.val;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
/**
 * Main class of application
 *
 */
public class App {
/**
 * Trying to parse command line args and provide bean with copying parameters.
 * Terminates program if it is not consistent or not valid
 * @param args Command line args array
 * @return Valiated copying parameters
 */
    public static CopyingArgs tryParseArgs(String[] args) {
        val parser = ArgumentParsers.newFor("prog").build()
            .defaultHelp(true)
            .description("Copy table contents to specified schema");
        parser.addArgument("--from_db").required(true)
            .help("source DB");
        parser.addArgument("--from_tables").required(true)
            .help("source tables");
        parser.addArgument("--to_db").required(true)
            .help("destination DB");
        parser.addArgument("--to_schema").setDefault("public")
            .help("destination schema");

        try {
            val parsedArgs = parser.parseArgs(args);
            String fromDb = parsedArgs.getString("from_db");
            String[] fromTables = parsedArgs.getString("from_tables").split(",");
            String toDb = parsedArgs.getString("to_db");
            String toSchema = parsedArgs.getString("to_schema");

            if (fromDb.equalsIgnoreCase(toDb) && toSchema.equalsIgnoreCase("public")) {
                throw new IllegalArgumentException();
            }

            return new CopyingArgs(fromDb, fromTables, toDb, toSchema);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Source is equal to destination. Aborted");
        }
// Something went wrong, aborting
        System.exit(1);
        return null;
    }

    public static void main(String[] args) {
        try {
            new TableCopier(tryParseArgs(args)).copyAll();
        } catch (SQLException e) {
            System.out.println("Copying error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("You forgot driver " + e.getMessage());
        }
    }

}
