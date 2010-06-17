package gov.loc.repository.bagger.ui.util;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.ConsoleView;

import java.awt.Image;
import java.util.Locale;

import javax.swing.UIManager;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.image.ImageSource;

public class ApplicationContextUtil {
	
	static {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
	}

	public static String getMessage(String propertyName) {
		return Application.instance().getApplicationContext().getMessage(
				propertyName, null, propertyName, Locale.getDefault());
	}

	public static Image getImage(String imageName) {
		ImageSource source = (ImageSource) getService(ImageSource.class);
		return source.getImage(imageName);
	}

	public static BagView getBagView() {
		return BagView.getInstance();
	}

	public static ConsoleView getConsoleView() {
		return ConsoleView.getInstance();
	}

	public static DefaultBag getCurrentBag() {
		return getBagView().getBag();
	}

	private static ApplicationServices getApplicationServices() {
		return ApplicationServicesLocator.services();
	}

	public static void addConsoleMessageByProperty(String messagePropertyName) {
		getConsoleView().addConsoleMessages(getMessage(messagePropertyName));
	}
	
	public static void addConsoleMessage(String message) {
		getConsoleView().addConsoleMessages(message);
	}

	@SuppressWarnings("unchecked")
	private static Object getService(Class serviceType) {
		return getApplicationServices().getService(serviceType);
	}
}
