package gov.loc.repository.transfer.components.utilities;

import java.io.InputStream;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class StreamGobbler extends Thread {
	private InputStream is;
	private Writer out = null;
	    
	public StreamGobbler(InputStream is, Writer out)
    {
        this.is = is;
        this.out = out;
    }

	public StreamGobbler(InputStream is)
    {
        this.is = is;
    }
	
	
	@Override
    public void run()
    {
        try
        {            
        	InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null)
            {
                if (out != null)
                {
                	System.out.println("LINE: " + line);
                	out.write(line + "\r\n");
                }
            }
        }
        catch (IOException ex)
        {              
        }
    }
	
}
