package shell.config;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class Config {
    public Path homeDirectory = Paths.get(System.getProperty("user.home"));
}
