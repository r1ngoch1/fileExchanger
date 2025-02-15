package ru.royal.fileExchanger.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.royal.fileExchanger.service.DirectoryInitService;

@Component
public class AppStartupRunner implements CommandLineRunner {

    private final DirectoryInitService directoryInitService;

    public AppStartupRunner(DirectoryInitService directoryInitService) {
        this.directoryInitService = directoryInitService;
    }

    @Override
    public void run(String... args) {
        directoryInitService.createRootDirectoryIfNotExists();
    }
}

