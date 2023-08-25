package com.designwright.research.plugins;

import com.designwright.research.utils.annotations.CommandController;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mojo(name = "test", defaultPhase = LifecyclePhase.COMPILE)
public class CommandApiPlugin extends AbstractMojo {

    @Parameter(
            defaultValue = "${project.build.directory}/classes/",
            property = "outputLocation"
    )
    private String outputLocation;
    @Parameter(
            defaultValue = "swagger.yml",
            property = "fileName"
    )
    private String fileName;
    @Parameter(defaultValue = "${plugin}", readonly = true)
    private PluginDescriptor pluginDescriptor;
    @Component
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        List<String> classpathElements;
        try {
            classpathElements = project.getCompileClasspathElements();
            List<URL> projectClasspathList = new ArrayList<>();
            for (String element : classpathElements) {
                try {
                    projectClasspathList.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new MojoExecutionException(element + " is an invalid classpath element", e);
                }
            }

            URLClassLoader loader = new URLClassLoader(projectClasspathList.toArray(new URL[0]));
            // ... and now you can pass the above classloader to Reflections
            Reflections reflections = new Reflections(project.getGroupId());
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(CommandController.class);
            int i = 0;
        } catch (DependencyResolutionRequiredException e) {
            new MojoExecutionException("Dependency resolution failed", e);
        }
    }
}