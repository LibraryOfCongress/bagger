package gov.loc.repository.bagger.exceptionhandling;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.util.GuiStandardUtils;
import org.springframework.richclient.core.UIConstants;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.BadCredentialsException;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Min;
import org.hibernate.validator.Max;
import org.hibernate.validator.ClassValidator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;

/**
 * @author Geoffrey De Smet
 */
public class ExceptionHandlingView extends AbstractView {

    /**
     * IMPORTANT: This code isn't a good example of how to write a view.
     * It's just here to prove how the exception handlers work.
     * Take a look at the application context to see how the exception handler(s) are configured.
     */
    protected JComponent createControl() {
        TableLayoutBuilder layoutBuilder = new TableLayoutBuilder();

        layoutBuilder.cell(new JLabel("This page demonstrates the exception handling."));
        layoutBuilder.row();
        layoutBuilder.cell(new JLabel("Push the buttons and see which exception handler gets choosen."));
        layoutBuilder.row();
        layoutBuilder.cell(new JLabel("That defines the log level and the dialog shown."));
        layoutBuilder.row();
        layoutBuilder.unrelatedGapRow();

        JButton badCredentials = new JButton(new AbstractAction("Login with bad credentials"){
            public void actionPerformed(ActionEvent e) {
                loginWithBadCredentials();
            }
        });
        layoutBuilder.cell(badCredentials);
        layoutBuilder.row();
        layoutBuilder.relatedGapRow();

        JButton accessDenied = new JButton(new AbstractAction("Do something you don't have access to"){
            public void actionPerformed(ActionEvent e) {
                denyAccess();
            }
        });
        layoutBuilder.cell(accessDenied);
        layoutBuilder.row();
        layoutBuilder.relatedGapRow();

        JButton invalidPerson = new JButton(new AbstractAction("Validate a person with a null name and age 1981"){
            public void actionPerformed(ActionEvent e) {
                validateInvalidPerson();
            }
        });
        layoutBuilder.cell(invalidPerson);
        layoutBuilder.row();
        layoutBuilder.relatedGapRow();

        JButton nullPointer = new JButton(new AbstractAction("Cause a NumberFormatException"){
            public void actionPerformed(ActionEvent e) {
                causeNumberFormatException();
            }
        });
        layoutBuilder.cell(nullPointer);
        layoutBuilder.row();
        layoutBuilder.relatedGapRow();

        JButton stackOverflow = new JButton(new AbstractAction("Cause a stack overflow error"){
            public void actionPerformed(ActionEvent e) {
                causeStackOverflow();
            }
        });
        layoutBuilder.cell(stackOverflow);
        layoutBuilder.row();

        JPanel panel = layoutBuilder.getPanel();
        panel.setBorder(GuiStandardUtils.createEvenlySpacedBorder(UIConstants.ONE_SPACE));
        return panel;
    }

    private void loginWithBadCredentials() {
        throw new BadCredentialsException("Wrong username/password");
    }

    private void denyAccess() {
        throw new AccessDeniedException("You don't have access to do this");
    }

    private void validateInvalidPerson() {
        ClassValidator validator = new ClassValidator(ValidPerson.class);
        ValidPerson invalidPerson = new ValidPerson();
        invalidPerson.setAge(1981);
        validator.assertValid(invalidPerson);
    }

    public static class ValidPerson {

        private String name;
        private int age;

        @NotNull
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Min(0) @Max(200)
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }

    private void causeNumberFormatException() {
        Integer.parseInt("eight");
    }

    /**
     * Overflows in Sun's JRE after 1024 recursive calls
     */
    private int causeStackOverflow() {
        return causeStackOverflow() + 1 + causeStackOverflow();
    }

}
