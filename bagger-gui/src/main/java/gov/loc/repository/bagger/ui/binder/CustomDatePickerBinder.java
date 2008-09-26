/*
 * Copyright 2002-2007 the original author or authors.
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
package gov.loc.repository.bagger.ui.binder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXDatePicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.support.CustomBinding;

public class CustomDatePickerBinder extends AbstractBinder {

    protected CustomDatePickerBinder() {
        super(Date.class);
    }

    protected JComponent createControl(Map context) {
        return new JXDatePicker();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        final JXDatePicker datePicker = (JXDatePicker)control;
        return new CustomBinding(formModel, formPropertyPath, Date.class) {

            protected JComponent doBindControl() {
                datePicker.setDate((Date)getValue());
                datePicker.getEditor().addPropertyChangeListener("value", new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        controlValueChanged(datePicker.getDate());
                    }
                });
                return datePicker;
            }

            protected void readOnlyChanged() {
                datePicker.setEnabled(isEnabled() && !isReadOnly());
            }

            protected void enabledChanged() {
                datePicker.setEnabled(isEnabled() && !isReadOnly());
            }

            protected void valueModelChanged(Object newValue) {
                datePicker.setDate((Date)newValue);
            }
        };
    }
}