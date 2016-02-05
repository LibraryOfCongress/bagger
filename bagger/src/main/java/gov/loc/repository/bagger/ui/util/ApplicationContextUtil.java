package gov.loc.repository.bagger.ui.util;

import java.awt.Image;
import java.util.Locale;

import javax.swing.UIManager;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.image.ImageSource;

import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.ConsoleView;

public class ApplicationContextUtil {

  static {
    UIManager.put("FileChooser.readOnly", Boolean.TRUE);
  }

  public static String getMessage(String propertyName) {
    return Application.instance().getApplicationContext().getMessage(propertyName, null, propertyName, Locale.getDefault());
  }

  public static Image getImage(String imageName) {
    ImageSource source = (ImageSource) ApplicationServicesLocator.services().getService(ImageSource.class);
    return source.getImage(imageName);
  }

  public static BagView getBagView() {
    BagView bagView = (BagView) Application.instance().getApplicationContext().getBean("myBagView");
    return bagView;
  }

  public static void addConsoleMessage(String message) {
    ConsoleView consoleView = (ConsoleView) Application.instance().getApplicationContext().getBean("myConsoleView");
    consoleView.addConsoleMessages(message);
  }

}
