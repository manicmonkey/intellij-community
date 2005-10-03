package com.intellij.uiDesigner.propertyInspector.renderers;

import com.intellij.psi.PsiNameHelper;
import com.intellij.uiDesigner.UIDesignerBundle;

import java.text.MessageFormat;

/**
 * @author Anton Katilin
 * @author Vladimir Kondratyev
 */
public final class ClassToBindRenderer extends LabelPropertyRenderer{
  public void customize(final Object value){
    final String text;

    if(value != null){
      final String fqName = (String)value;
      final String className = PsiNameHelper.getShortClassName(fqName);
      if(fqName.length() == className.length()){ // class in default package
        text = className;
      }
      else{
        final String packageName = fqName.substring(0, fqName.length() - className.length() - 1);
        text = UIDesignerBundle.message("class.in.package", className, packageName);
      }
    }
    else{
      text = null;
    }

    setText(text);
  }
}
