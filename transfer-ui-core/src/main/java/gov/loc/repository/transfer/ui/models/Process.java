package gov.loc.repository.transfer.ui.models;

import java.util.List;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Process extends Base<Long> {
    protected Boolean suspended;
    protected List<Comment> comments;
}
