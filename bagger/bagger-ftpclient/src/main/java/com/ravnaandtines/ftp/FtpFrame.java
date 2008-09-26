/***************************************************************************
                                FtpFrame.java
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
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class FtpFrame extends Frame {

  //Construct the frame
  BorderLayout borderLayout1 = new BorderLayout();
  Label Username = new Label();
  Label Password = new Label();
  Label Hostname = new Label();
  Label banner = new Label();
  Panel holder = new Panel();
  TextField UsernameEdit = new TextField();
  TextField PasswordEdit = new TextField();
  TextField HostnameEdit = new TextField();
  Button doit = new Button("Connect");
  GridBagLayout gl = new GridBagLayout();
  GridBagConstraints gc = new GridBagConstraints();

  public FtpFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try  {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
//Component initialization

  private void jbInit() throws Exception  {
    this.setBackground(SystemColor.menu);
    this.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("FTP Client");

    Container pane = this;

    Container stick = new Panel();
    stick.setLayout(new FlowLayout(FlowLayout.CENTER));

    Properties settings = new Properties();
    try {
      File propfile = new File("ftp.ini");
      if(propfile.exists() && propfile.canRead())
      {
        settings.load(new FileInputStream(propfile));
      }
    } catch (IOException ioex) {}

    Username.setText("Username:");
    UsernameEdit.setText(settings.getProperty("Username", ""));
    Password.setText("Password:");
    PasswordEdit.setEchoChar('*');
    PasswordEdit.setText(settings.getProperty("Password", ""));
    Hostname.setText("Hostname:");
    HostnameEdit.setText(settings.getProperty("Hostname", ""));
    banner.setText("Enter FTP parameters");
    holder.setLayout(gl);
    doit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FtpSession act = new FtpSession(UsernameEdit.getText(),
          PasswordEdit.getText(), HostnameEdit.getText());
        Thread th = new Thread(act);
        th.start();
      }
    });
    pane.add(stick, BorderLayout.SOUTH);
    stick.add(doit);
    pane.add(holder, BorderLayout.CENTER);
    //pane.add(captions, BorderLayout.WEST);
    pane.add(banner, BorderLayout.NORTH);


    gc.gridx = 0;
    gc.gridy = 0;
    gc.gridwidth = 2;
    gc.gridheight = 1;
    gc.fill = GridBagConstraints.NONE;
    gc.ipadx =1;
    gc.ipady = 1;
    gc.anchor = GridBagConstraints.WEST;
    gc.weightx = 0;
    gc.weighty = 0;

    holder.add(Username, gc);

    gc.gridx = 2;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    holder.add(UsernameEdit, gc);

    gc.gridx = 0;
    gc.gridy = 1;
    gc.gridwidth = 2;
    gc.weightx = 0;
    gc.fill = GridBagConstraints.NONE;
    holder.add(Password, gc);

    gc.gridx = 2;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    holder.add(PasswordEdit, gc);

    gc.gridx = 0;
    gc.gridy = 2;
    gc.gridwidth = 2;
    gc.weightx = 0;
    gc.fill = GridBagConstraints.NONE;
    holder.add(Hostname, gc);

    gc.gridx = 2;
    gc.gridwidth = 3;
    gc.weightx = 1;
    gc.fill = GridBagConstraints.HORIZONTAL;
    holder.add(HostnameEdit, gc);
  }
//Overriden so we can exit on System Close

  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
    	this.hide();
      //System.exit(0);
    }
  }
}

