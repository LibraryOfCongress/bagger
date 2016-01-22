package gov.loc.repository.bagger.ui;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BagTextPane extends JTextPane {
  private static final long serialVersionUID = -505900021814525136L;

  protected static final Logger log = LoggerFactory.getLogger(BagTextPane.class);

  private StyledDocument document;
  private String message = "";
  private Color textBackground = new Color(240, 240, 240);

  public BagTextPane(String message) {
    super();

    this.message = message;
    this.buildDocument();
    this.setStyledDocument(document);
    this.setAutoscrolls(true);
    this.setEditable(false);
    this.setBackground(textBackground);
  }

  public void setMessage(String message) {
    this.message = message;
    this.buildDocument();
    this.setStyledDocument(document);
  }

  public String getMessage() {
    return this.message;
  }

  private void buildDocument() {
    StyleContext context = new StyleContext();
    document = new DefaultStyledDocument(context);

    Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
    StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
    StyleConstants.setFontSize(style, 14);
    StyleConstants.setSpaceAbove(style, 4);
    StyleConstants.setSpaceBelow(style, 4);
    // Insert content
    try {
      document.insertString(document.getLength(), message, style);
    }
    catch (BadLocationException badLocationException) {
      log.error(badLocationException.getMessage());
    }

    SimpleAttributeSet attributes = new SimpleAttributeSet();
    StyleConstants.setBold(attributes, true);
    StyleConstants.setItalic(attributes, true);

    // Third style for icon/component
    Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);

    Icon icon = new ImageIcon("Computer.gif");
    JLabel label = new JLabel(icon);
    StyleConstants.setComponent(labelStyle, label);
  }

  @Override
  public boolean requestFocusInWindow() {
    return this.requestFocusInWindow();
  }
}