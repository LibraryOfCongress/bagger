/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.beans.PropertyAccessException;
import org.springframework.richclient.application.ApplicationLauncher;
import org.apache.commons.logging.LogFactory;

/**
 * Main driver that starts the Bagger spring rich client application.
 */
public class BaggerApplication {

    public static void main(String[] args) {
        String rootContextDirectoryClassPath = "/gov/loc/repository/bagger/ctx";

        String startupContextPath = rootContextDirectoryClassPath + "/common/richclient-startup-context.xml";

        String richclientApplicationContextPath = rootContextDirectoryClassPath
                + "/common/richclient-application-context.xml";

        String businessLayerContextPath = rootContextDirectoryClassPath + "/common/business-layer-context.xml";

        try {
        	new ApplicationLauncher(startupContextPath, new String[] { richclientApplicationContextPath,
            		businessLayerContextPath });
        } catch (IllegalStateException ex1) {
            LogFactory.getLog(BaggerApplication.class).error("IllegalStateException during startup", ex1);
            JOptionPane.showMessageDialog(new JFrame(), "An illegal state error occured.\n", "Bagger startup error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (PropertyAccessException ex) {
            LogFactory.getLog(BaggerApplication.class).error("PropertyAccessException during startup", ex);
            JOptionPane.showMessageDialog(new JFrame(), "An error occured loading properties.\n", "Bagger startup error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (RuntimeException e) {
            LogFactory.getLog(BaggerApplication.class).error("RuntimeException during startup", e);
        	String msg = e.getMessage();
        	if (msg.contains("SAXParseException")) {
                JOptionPane.showMessageDialog(new JFrame(), "An error occured parsing application context.  You may have no internet access.\n" , "Bagger startup error!", JOptionPane.ERROR_MESSAGE);
        	} else {
                JOptionPane.showMessageDialog(new JFrame(), "An error occured during startup.\n" , "Bagger startup error!", JOptionPane.ERROR_MESSAGE);
        	}
            System.exit(1);
        }
    }

}
