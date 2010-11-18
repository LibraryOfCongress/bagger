/***************************************************************************
                                FtpSession.java
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

import java.awt.Dialog;

public class FtpSession implements Runnable {
  private String Username;
  private String Password;
  private String Hostname;


  public FtpSession (String Username, String Password, String Hostname)
  {
      this.Username = Username;
      this.Password = Password;
      this.Hostname = Hostname;
  }

  private FtpSession() {
  }

  public void run()
  {
      TinesLinlyn ftp = new TinesLinlyn(Hostname, Username, Password);
      try {
          Dialog j = new FtpSessionDlg(FTP.frame, ftp, Hostname);
          java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
          java.awt.Dimension frameSize = j.getSize();
          if (frameSize.height > screenSize.height)
        	  frameSize.height = screenSize.height;
          if (frameSize.width > screenSize.width)
        	  frameSize.width = screenSize.width;
          j.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
          j.show();
      } finally {
        Password = null;
        ftp.ftpLogout();
      }
  }

}