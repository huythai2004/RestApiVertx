package org.example.config;

import com.zandero.cmd.CommandBuilder;
import com.zandero.cmd.CommandLineException;
import com.zandero.cmd.CommandLineParser;
import com.zandero.cmd.option.IntOption;
import com.zandero.cmd.option.StringOption;
import com.zandero.cmd.option.CommandOption;
import com.zandero.settings.Settings;

public class ServerSettings extends Settings {
    private static final String PORT = "port";
    private static final String POOL_SIZE = "poolSize";
    private static final String LOG_FILE = "logFile";

    private final CommandBuilder builder;

    public ServerSettings() {
        CommandOption<Integer> port = new IntOption("p")
                .longCommand(PORT)
                .setting(PORT)
                .description("The port to listen on")
                .defaultsTo(8080);

        CommandOption<Integer> poolSize = new IntOption("P")
                .longCommand(POOL_SIZE)
                .setting(POOL_SIZE)
                .description("Worker thread pool size")
                .defaultsTo(10);

        CommandOption<String> logging = new StringOption("l")
                .longCommand(LOG_FILE)
                .setting(LOG_FILE)
                .description("Log file to use")
                .defaultsTo(null);

        builder = new CommandBuilder();
        builder.add(port);
        builder.add(poolSize);
        builder.add(logging);
    }

    public void parse(String[] args) throws CommandLineException {
        CommandLineParser parser = new CommandLineParser(builder);
        putAll(parser.parse(args));
    }

    public int getPort() {
        return getInt(PORT);
    }

    public String getLog() {
        return findString(LOG_FILE);
    }

    public int getPoolSize() {
        return getInt(POOL_SIZE);
    }
}
