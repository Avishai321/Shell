package shell.core;

import lombok.Getter;
import lombok.Setter;
import shell.config.Config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ShellContext {
    private volatile boolean isRunning;

    private Path pwd;

    private final Map<String, String> exportedEnvVar;
    private final Map<String, String> localEnvVars;

    public ShellContext() {
        this.isRunning = true;
        this.pwd = Config.homeDirectory;

        this.exportedEnvVar = new HashMap<>(System.getenv());
        this.localEnvVars = new HashMap<>();
    }
}
