package com.intellij.execution.junit2.configuration;

import com.intellij.execution.ExecutionUtil;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.configurations.RuntimeConfigurationWarning;
import com.intellij.execution.junit.JUnitUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.CollectUtil;
import com.intellij.util.containers.ConvertingIterator;
import com.intellij.util.containers.HashSet;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RunConfigurationModule implements JDOMExternalizable {
  private static final Logger LOG = Logger.getInstance("#com.intellij.execution.junit2.configuration.RunConfigurationModule");
  @NonNls private static final String ELEMENT = "module";
  @NonNls private static final String ATTRIBUTE = "name";
  private String myModuleName = "";
  private final Project myProject;
  private final boolean myClassesInLibraries;

  public RunConfigurationModule(final Project project, final boolean classesInLibs) {
    myProject = project;
    myClassesInLibraries = classesInLibs;
  }

  public void readExternal(final Element element) throws InvalidDataException {
    final List<Element> modules = (List<Element>)element.getChildren(ELEMENT);
    LOG.assertTrue(modules.size() <= 1);
    if (modules.size() == 1) {
      final Element module = modules.get(0);
      final String moduleName = module.getAttributeValue(ATTRIBUTE);  //we are unable to set 'null' module from 'not null' one
      if (moduleName != null && moduleName.length() > 0){
        myModuleName = moduleName;
      }
    }
  }

  public void writeExternal(final Element parent) throws WriteExternalException {
    final Element element = new Element(ELEMENT);
    element.setAttribute(ATTRIBUTE, getModuleName());
    parent.addContent(element);
  }

  public void init() {
    if (getModuleName().trim().length() > 0) return;
    final Module[] modules = getModuleManager().getModules();
    if (modules.length > 0) setModuleName(modules[0].getName());
  }

  public Project getProject() { return myProject; }

  public Module getModule() { return findModule(getModuleName()); }

  public Module findModule(final String moduleName) { return getModuleManager().findModuleByName(moduleName); }

  public void setModule(final Module module) { setModuleName(module.getName()); }

  public String getModuleName() { return myModuleName != null ? myModuleName : ""; }

  public void setModuleName(final String moduleName) {
    myModuleName = moduleName != null ? moduleName : "";
  }

  private ModuleManager getModuleManager() {
    return ModuleManager.getInstance(myProject);
  }

  public PsiClass findClass(final String qualifiedName) {
    if (qualifiedName == null) return null;
    return JUnitUtil.findPsiClass(qualifiedName.replace('$', '.'), getModule(), myProject, myClassesInLibraries);
  }

  public static Collection<Module> getModulesForClass(final Project project, final String className) {
    if (project.isDefault()) return Arrays.asList(ModuleManager.getInstance(project).getModules());
    PsiDocumentManager.getInstance(project).commitAllDocuments();
    final PsiClass[] possibleClasses = PsiManager.getInstance(project).findClasses(className,
                                                                             GlobalSearchScope.projectScope(project));
    final HashSet<Module> modules = CollectUtil.SKIP_NULLS.toSet(possibleClasses,
                                                           ConvertingIterator.composition(ExecutionUtil.FILE_OF_CLASS,
                                                                                          ExecutionUtil.fileToModule(project)));
    if (modules.isEmpty()) {
      return Arrays.asList(ModuleManager.getInstance(project).getModules());
    }
    else {
      return ExecutionUtil.collectModulesDependsOn(modules);
    }
  }

  public void checkForWarning() throws RuntimeConfigurationException {
    final Module module = getModule();
    if (module != null) {
      if (ModuleRootManager.getInstance(module).getJdk() == null) {
        throw new RuntimeConfigurationWarning(ExecutionBundle.message("no.jdk.specified.for.module.warning.text", module.getName()));
      }
      else {
        return;
      }
    }else {
    if (myModuleName == null || myModuleName.trim().length() == 0) {
      throw new RuntimeConfigurationError(ExecutionBundle.message("module.not.specified.error.text"));
    }
    else {
      throw new RuntimeConfigurationError(ExecutionBundle.message("module.doesn.t.exist.in.project.error.text", myModuleName));
    }
    }
  }

  public PsiClass checkClassName(final String className, final String errorMessage) throws RuntimeConfigurationException {
    if (className == null || className.length() == 0) {
      throw new RuntimeConfigurationError(errorMessage);
    }
    return findNotNullClass(className);
  }

  public PsiClass findNotNullClass(final String className) throws RuntimeConfigurationWarning {
    final PsiClass psiClass = findClass(className);
    if (psiClass == null) {
      throw new RuntimeConfigurationWarning(
        ExecutionBundle.message("class.not.found.in.module.error.message", className, getModuleName()));
    }
    return psiClass;
  }

  public PsiClass checkModuleAndClassName(final String className, final String expectedClassMessage) throws RuntimeConfigurationException {
    checkForWarning();
    return checkClassName(className, expectedClassMessage);
  }

}
