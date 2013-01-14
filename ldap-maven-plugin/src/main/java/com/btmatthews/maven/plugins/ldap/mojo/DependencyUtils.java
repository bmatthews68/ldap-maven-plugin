package com.btmatthews.maven.plugins.ldap.mojo;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.*;
import org.sonatype.aether.RepositorySystemSession;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Brian
 * Date: 12/01/13
 * Time: 01:06
 * To change this template use File | Settings | File Templates.
 */
public class DependencyUtils {

    public static Set getDependencyArtifacts(final MavenProject project,
                                             final RepositorySystemSession repoSession,
                                             final ProjectDependenciesResolver projectDependenciesResolver) {

        DefaultDependencyResolutionRequest dependencyResolutionRequest = new DefaultDependencyResolutionRequest(project, repoSession);
        DependencyResolutionResult dependencyResolutionResult;

        try {
            dependencyResolutionResult = projectDependenciesResolver.resolve(dependencyResolutionRequest);
        } catch (final DependencyResolutionException ex) {
            return null;
        }

        final Set<Artifact> artifacts = new LinkedHashSet<Artifact>();
        if (dependencyResolutionResult.getDependencyGraph() != null
                && !dependencyResolutionResult.getDependencyGraph().getChildren().isEmpty()) {
            RepositoryUtils.toArtifacts(artifacts, dependencyResolutionResult.getDependencyGraph().getChildren(),
                    Collections.singletonList(project.getArtifact().getId()), null);
        }
        return artifacts;
    }
}
