
package com.intellij.ide.macro;

import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootsTraversing;
import com.intellij.ide.IdeBundle;

public final class SourcepathMacro extends Macro {
  public String getName() {
    return "Sourcepath";
  }

  public String getDescription() {
    return IdeBundle.message("macro.project.sourcepath");
  }

  public String expand(DataContext dataContext) {
    Project project = (Project)dataContext.getData(DataConstants.PROJECT);
    if (project == null) return null;
    return ProjectRootsTraversing.collectRoots(project, ProjectRootsTraversing.PROJECT_SOURCES).getPathsString();
  }
}
