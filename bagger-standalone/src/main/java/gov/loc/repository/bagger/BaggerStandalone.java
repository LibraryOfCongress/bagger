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

import org.springframework.richclient.application.ApplicationLauncher;
import org.apache.commons.logging.LogFactory;

/**
 * Main driver that starts the Bagger spring rich client application.
 */
public class BaggerStandalone {

    public static void main(String[] args) {
        String rootContextDirectoryClassPath = "/gov/loc/repository/bagger/ctx";

        String startupContextPath = rootContextDirectoryClassPath + "/common/richclient-startup-context.xml";

        String richclientApplicationContextPath = rootContextDirectoryClassPath
                + "/common/richclient-application-context.xml";

        String businessLayerContextPath = rootContextDirectoryClassPath + "/common/business-layer-context.xml";

        String securityContextPath = rootContextDirectoryClassPath + "/standalone/security-context.xml";

        try {
        	new ApplicationLauncher(startupContextPath, new String[] { richclientApplicationContextPath,
            		businessLayerContextPath, securityContextPath });
        } catch (RuntimeException e) {
            LogFactory.getLog(BaggerStandalone.class).error("RuntimeException during startup", e);
        }
    }

}