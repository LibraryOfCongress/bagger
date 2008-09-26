/***************************************************************************
                               LogTextPane.java
                             -------------------
    copyright            : (C) 2002 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the Artistic License as found in file           *
 *   TinesLinlyn.java                                                      *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                  *
 *                                                                         *
 ***************************************************************************/


package com.ravnaandtines.ftp;

import java.awt.*;
import java.io.*;

public class LogTextPane extends ScrollPane {

  private LogTextComponent child;
  private static String endl = System.getProperty("line.separator", "\r\n");

  public LogTextPane()
  {
    child = new LogTextComponent(this);
    add(child);
  }

  public void append(String s) { child.append(s); }
  public void viewEnd() { child.viewEnd();}
  public String getText() { return child.getText();}

  private void resize()
  {
    remove(child);
    invalidate();
    add(child);
    validate();
  }

  private class LogTextComponent extends Component
    implements java.awt.event.AdjustmentListener
  {
  private java.util.Vector text;
  private boolean partial;
  private FontMetrics fm = null;
  private int lineHeight = 0;
  private int width;
  private LogTextPane parent;

  public LogTextComponent(LogTextPane p) {
    setForeground(SystemColor.text);
    setBackground(SystemColor.textText);
    parent = p;
    text = new java.util.Vector(50);

    partial = false;
    parent.getHAdjustable().addAdjustmentListener(this);
    parent.getVAdjustable().addAdjustmentListener(this);

  }

  public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e)
  {
    repaint();
  }

  private void setupFont()
  {
    Font f = getFont();
    fm = Toolkit.getDefaultToolkit().getFontMetrics(f);

    lineHeight = fm.getMaxAscent()+fm.getMaxDescent();
    width = fm.stringWidth("************************");
  }

  private void addLine(String s)
  {
    if(partial)
    {
      int size = text.size()-1;
      String last = (String)text.elementAt(size);
      text.removeElementAt(size);
      s = last+s;
    }
    int w = fm.stringWidth(s);
    if(w > width) width = w;
    text.addElement(s);
  }

  public void append(String s)
  {
    if(null == fm) setupFont();
    boolean complete = s.endsWith(endl);
    BufferedReader r = new BufferedReader(
      new StringReader(s));

    try {
      for(String line = r.readLine();
          line != null;
          line = r.readLine())
      {
        addLine(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    parent.resize();
    partial = !complete;
  }

  public String getText()
  {
    StringBuffer s = new StringBuffer();
    int size = text.size()-1;
    for(int i=0; i<size; ++i)
    {
      s.append((String)text.elementAt(i));
      s.append(endl);
    }
    if(size > 0)
    {
      s.append((String)text.elementAt(size));
      if(!partial)
        s.append(endl);
    }

    return s.toString();
  }

  public Dimension getSize()
  {
    try {
      if(null == fm) setupFont();
    } catch (NullPointerException e)
    {
      return new Dimension(160, 100);
    }

    int nl = text.size();

    if(nl < 10) nl = 10;
    return new Dimension(width, lineHeight * nl);
  }

  public Dimension getMinimumSize()
  {
    return getSize();
  }

   public Dimension getPreferredSize()
  {
    return getSize();
  }

  public Dimension minimumSize()
  {
    return getSize();
  }

  public Dimension preferredSize()
  {
    return getSize();
  }

  public void paint(Graphics g)
  {
    if(null == fm) setupFont();
    Point topLeft = parent.getScrollPosition();
    Dimension view = parent.getViewportSize();

    int firstLine = (topLeft.y/lineHeight);
    int lastLine = 1 + ((topLeft.y+view.height)/lineHeight);
    int size = text.size();

    if(firstLine < size)
    for(int i = firstLine; i<size && i < lastLine; ++i)
    {
      g.drawString((String)text.elementAt(i),
        0, (i+1)*lineHeight);
    }
  }

  public void viewEnd()
  {
    if(lineHeight*text.size() < parent.getViewportSize().height)
    {
      parent.getVAdjustable().setValue(0);
    }
    else
    {
      Adjustable a = parent.getVAdjustable();
      a.setValue(a.getMaximum());
    }
  }

  } // end inner class

}
