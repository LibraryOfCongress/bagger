package gov.loc.repository.transfer.components.packaging.impl;

import gov.loc.repository.transfer.components.packaging.Unpackager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.zip.*;
import java.io.FileNotFoundException;


public class ZipPackager implements Unpackager
{
	private static final int BUF_SIZE = 4096;

	public void unpackage(File sourceFile, File destinationDirectory) throws Exception {		
		if (sourceFile == null)
		{
			throw new IllegalArgumentException("sourceFile is null");
		}
		if (! sourceFile.exists())
		{
			throw new FileNotFoundException(sourceFile.toString() + " not found");
		}
		if (destinationDirectory == null)
		{
			throw new IllegalArgumentException("destinationDirectory is null");
		}
		if (! destinationDirectory.exists())
		{
			destinationDirectory.mkdirs();
		}
		ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(sourceFile));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		while(zipEntry != null)
		{
			File outFile = new File(destinationDirectory, zipEntry.getName());
			if (zipEntry.isDirectory())
			{
				outFile.mkdirs();
			}
			else
			{
				FileOutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[BUF_SIZE];
				int bytes_read;
				while((bytes_read = zipInputStream.read(buf)) != -1)
				{
					out.write(buf, 0, bytes_read);
				}
				out.close();
			}
			zipInputStream.closeEntry();
			zipEntry = zipInputStream.getNextEntry();			
		}
		zipInputStream.close();				
	}
}
