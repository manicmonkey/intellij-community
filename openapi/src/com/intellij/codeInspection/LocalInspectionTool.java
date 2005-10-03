/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.codeInspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

/**
 * Implement this abstract class in order to provide new inspection tool functionality. The major API limitation here is
 * subclasses should be stateless. Thus <code>check&lt;XXX&gt;</code> methods will be called in no particular order and
 * instances of this class provided by {@link InspectionToolProvider#getInspectionClasses()} will be created on demand.
 * The other important thing is problem anchors (PsiElements) reported by <code>check&lt;XXX&gt;</code> methods should
 * lie under corresponding first parameter of one method.
 */
public abstract class LocalInspectionTool {
  /**
   * Override this to report problems at method level.
   *
   * @param method     to check.
   * @param manager    InspectionManager to ask for ProblemDescriptor's from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return <code>null</code> if no problems found or not applicable at method level.
   */
  @Nullable
  public ProblemDescriptor[] checkMethod(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  /**
   * Override this to report problems at class level.
   *
   * @param aClass     to check.
   * @param manager    InspectionManager to ask for ProblemDescriptor's from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return <code>null</code> if no problems found or not applicable at class level.
   */
  @Nullable
  public ProblemDescriptor[] checkClass(PsiClass aClass, InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  /**
   * Override this to report problems at field level.
   *
   * @param field      to check.
   * @param manager    InspectionManager to ask for ProblemDescriptor's from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return <code>null</code> if no problems found or not applicable at field level.
   */
  @Nullable
  public ProblemDescriptor[] checkField(PsiField field, InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  /**
     * Override this to report problems at file level.
     *
     * @param file       to check.
     * @param manager    InspectionManager to ask for ProblemDescriptor's from.
     * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
     * @return <code>null</code> if no problems found or not applicable at field level.
     */
  @Nullable
  public ProblemDescriptor[] checkFile(PsiFile file, InspectionManager manager, boolean isOnTheFly) {
    return null;
  }


  public abstract String getGroupDisplayName();

  public abstract String getDisplayName();

  /**
   * @return short name that is used in two cases: \inspectionDescriptions\&lt;short_name&gt;.html resource may contain short inspection
   *         description to be shown in "Inspect Code..." dialog and also provide some file name convention when using offline
   *         inspection or export to HTML function. Should be unique among all inspections.
   */
  @NonNls public abstract String getShortName();

  /**
   * @return highlighting level for this inspection tool that is used in default settings
   */
  public HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  public boolean isEnabledByDefault() {
    return false;
  }

  /**
   * @return null if no UI options required
   */
  @Nullable
  public JComponent createOptionsPanel() {
    return null;
  }

  /**
   * @return descriptive name to be used in "suppress" comments and annotations,
   *         must consist of [a-zA-Z_0-9]+
   */
  @NonNls public String getID() {
    return getShortName();
  }

  /**
   * Read in settings from xml config. Default implementation uses DefaultJDOMExternalizer so you may use public fields like <code>int TOOL_OPTION</code> to store your options.
   *
   * @param node to read settings from.
   * @throws InvalidDataException
   */
  public void readSettings(Element node) throws InvalidDataException {
    DefaultJDOMExternalizer.readExternal(this, node);
  }

  /**
   * Store current settings in xml config. Default implementation uses DefaultJDOMExternalizer so you may use public fields like <code>int TOOL_OPTION</code> to store your options.
   *
   * @param node to store settings to.
   * @throws WriteExternalException
   */
  public void writeSettings(Element node) throws WriteExternalException {
    DefaultJDOMExternalizer.writeExternal(this, node);
  }
}
