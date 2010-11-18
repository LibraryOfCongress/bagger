/***************************************************************************
                              FTPSessionDlg.java
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
import java.awt.event.*;

public class FtpSessionDlg extends Dialog {
  Panel panel1 = new Panel();
  BorderLayout borderLayout1 = new BorderLayout();
  Panel panel2 = new Panel();
  TextField rcd = new TextField("", 30);
  Checkbox ascii = new Checkbox("ASCII mode", false);

  LogTextPane feedback = new LogTextPane();

  TinesLinlyn ftp;
  static String theDir = null;

  Label prog = null;

  public FtpSessionDlg(Frame frame, TinesLinlyn ftp, String host) {
    super(frame, "FTP Session on "+host, true);
    this.ftp = ftp;
    try  {
      jbInit();
      ftp.setReporter(feedback);
      pack();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setBackground(SystemColor.menu);
    panel1.setLayout(borderLayout1);
    add(panel1);

    Panel topStrip = new Panel();
    topStrip.setLayout(new FlowLayout());
    topStrip.add(rcd);
    panel1.add(topStrip, BorderLayout.NORTH);

    Button cd = new Button("cd");
    topStrip.add(cd);
    cd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cd_actionPerformed(e);
      }
    });

    Button md = new Button("mkdir");
    topStrip.add(md);
    md.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        mkdir_actionPerformed(e);
      }
    });

    Button del = new Button("delete");
    topStrip.add(del);
    del.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        del_actionPerformed(e);
      }
    });

    Button get = new Button("get");
    topStrip.add(get);
    get.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        get_actionPerformed(e);
      }
    });

    panel1.add(feedback, BorderLayout.CENTER);

    Panel s = new Panel();
    BorderLayout l = new BorderLayout();
    s.setLayout(l);

    s.add(ascii, BorderLayout.NORTH);
    s.add(prog = new Label("--Ready to Upload--"), BorderLayout.CENTER);

    Button Upload = new Button("Upload...");
    Upload.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Upload_actionPerformed(e);
      }
    });
    panel2.add(Upload, null);
    Button list = new Button("ls -al");
    panel2.add(list, null);
    list.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        list_actionPerformed(e);
      }
    });

    Button nlist = new Button("ls");
    panel2.add(nlist, null);
    nlist.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nlist_actionPerformed(e);
      }
    });

    Button pwd = new Button("look");
    panel2.add(pwd, null);
    pwd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        pwd_actionPerformed(e);
      }
    });
    s.add(panel2, BorderLayout.SOUTH);
    panel1.add(s, BorderLayout.SOUTH);

    this.setSize(new Dimension(500, 400));
  }

  void Upload_actionPerformed(ActionEvent e) {
    FileDialog f = new FileDialog((Frame)getParent(),
        "Select file", FileDialog.LOAD);
    if(theDir != null)
      f.setDirectory(theDir);
    f.show();

    String file = f.getFile();
    if(null == file) return;

    theDir = f.getDirectory();


    try {
       FileInputStream fx = new FileInputStream(theDir+file);
       ByteArrayOutputStream b = new ByteArrayOutputStream();
       byte [] bucket = new byte[4096];
       int n;

       while( (n = fx.read(bucket)) >= 0)
       {
        b.write(bucket, 0, n);
       }
       fx.close();
       b.close();

       ftp.upload(file, b.toByteArray(), prog, ascii.getState());

    } catch (Exception ex) {ex.printStackTrace();}
  }

  void cd_actionPerformed(ActionEvent e)
  {
    if(rcd.getText().length() == 0)
      return;
    ftp.setDir(rcd.getText());
    rcd.setText("");
    pwd_actionPerformed(e);
  }

  void mkdir_actionPerformed(ActionEvent e)
  {
    if(rcd.getText().length() == 0)
      return;
    ftp.mkDir(rcd.getText());
    rcd.setText("");
  }

  void del_actionPerformed(ActionEvent e)
  {
    if(rcd.getText().length() == 0)
      return;
    ftp.del(rcd.getText());
    rcd.setText("");
  }

  void pwd_actionPerformed(ActionEvent e)
  {
    ftp.pwd();
  }

  void list_actionPerformed(ActionEvent e)
  {
    ftp.list();
  }

  void nlist_actionPerformed(ActionEvent e)
  {
    ftp.nlist();
  }

  void get_actionPerformed(ActionEvent e) {
    if(rcd.getText().length() == 0)
      return;

    FileDialog f = new FileDialog((Frame)getParent(),
        "Select file", FileDialog.SAVE);
    f.setFile(rcd.getText());
    if(theDir != null)
      f.setDirectory(theDir);
    f.show();

    String file = f.getFile();
    if(null == file) return;

    theDir = f.getDirectory();
    double start = System.currentTimeMillis();

    try {
       FileOutputStream fx = new FileOutputStream(theDir+file);
       byte [] bucket = ftp.download(file, ascii.getState());
       fx.write(bucket);
       fx.close();

       double endt = (System.currentTimeMillis()-start)/1000.0;
       prog.setText("All "+bucket.length+" bytes in "+endt+"s = "+
            (bucket.length/endt)+" bytes/sec");

       feedback.append(prog.getText()+System.getProperty("line.separator", "\r\n"));
       feedback.viewEnd();
       prog.getParent().validate();

    } catch (Exception ex) {ex.printStackTrace();}
  }

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      setVisible(false);
      dispose();
    }
  }

}

