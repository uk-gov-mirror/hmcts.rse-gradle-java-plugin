package uk.gov.hmcts;

import lombok.SneakyThrows;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.plugins.quality.CheckstylePlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

public class JavaPlugin implements Plugin<Project> {

    @SneakyThrows
    public void apply(Project project) {
        project.getPlugins().apply(CheckstylePlugin.class);
        CheckstyleExtension ext = project.getExtensions().getByType(CheckstyleExtension.class);
        ext.setToolVersion("8.31");
        ext.setMaxWarnings(0);
        ext.setMaxErrors(0);
        ext.setIgnoreFailures(false);
        project.afterEvaluate(evaluatedProject -> {
            File checkstyleConfig = writeCheckstyleConfig(evaluatedProject);

            for (Checkstyle checkstyle : evaluatedProject.getTasks().withType(Checkstyle.class)) {
                checkstyle.setConfigFile(checkstyleConfig);
            }
        });
    }

    @SneakyThrows
    private File writeCheckstyleConfig(Project project) {
        File dir = new File(project.getBuildDir(), "config/checkstyle");
        dir.mkdirs();
        File checkstyleConfig = new File(dir, "checkstyle.xml");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("hmcts-checkstyle.xml")) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(checkstyleConfig))) {
                writer.write(s.next());
                return checkstyleConfig;
            }
        }
    }
}