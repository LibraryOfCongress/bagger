package gov.loc.repository.transfer.ui.utilities ;

import java.io.*;
import javax.servlet.http.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/**
 * This servlet examines a batch file and returns the package name.
 *
 * @author Jon Steinbach
 */
public class ProcessBatchFile extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
     * This method is overriden from the base class to handle the
     * get request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                   throws IOException
    {
        String batchName = null;

    	String fileName = (String) request.getParameter("batchFile");

        PrintWriter output = response.getWriter();
        //set the content type
        response.setContentType("text/xml");
    	try {
        	if (fileName != null) {
            	File file = new File(fileName);
            	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            	DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            	Document doc = docBuilder.parse(file);
            	doc.getDocumentElement().normalize ();
            	NodeList list = doc.getElementsByTagName("batch"); 
            	if (list != null) {
                	Element elem = (Element) list.item(0);
                	if (elem != null) {
                		batchName = elem.getAttribute("name");
                 	}
            	}
        	}    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
            output.println(batchName);
            output.close();    		
    	}
    }
}
