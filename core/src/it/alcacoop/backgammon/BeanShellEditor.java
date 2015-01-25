/*
 * Created on 01.11.2004
 */
package it.alcacoop.backgammon;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JViewport;
import javax.swing.plaf.TextUI;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

/**
 * A TextArea for BeanShell-Skripts. Including Syntax-Highlighting
 * 
 * @author bodum
 */
public class BeanShellEditor extends JEditorPane {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Create the TextArea
   */
  public BeanShellEditor() {
    setDocument(new SyntaxDocument());

    EditorKit editorKit = new StyledEditorKit() {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public Document createDefaultDocument() {
        return new SyntaxDocument();
      }
    };

    setEditorKitForContentType("text/beanshell", editorKit);
    setContentType("text/beanshell");
    
    
  }

  /**
   * Override to get no Line-Wraps
   */
  public boolean getScrollableTracksViewportWidth() {
    if (getParent() instanceof JViewport) {
      JViewport port = (JViewport) getParent();
      TextUI textUI = getUI();
      int w = port.getWidth();
      textUI.getMinimumSize(this);
      textUI.getMaximumSize(this);
      Dimension pref = textUI.getPreferredSize(this);
      
      if ((w >= pref.width)) {
        return true;
      }
    }
    return false;
  }

}
