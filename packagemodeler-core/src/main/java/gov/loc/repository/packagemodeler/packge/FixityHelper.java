package gov.loc.repository.packagemodeler.packge;


import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FixityHelper
{
	private static final Log log = LogFactory.getLog(FixityHelper.class);	
	
	public static <T extends Fixity> boolean isConsistent(Set<T> fixitySet1, Set<T> fixitySet2)
	{
		boolean matchFound = false;
		log.debug(MessageFormat.format("Checking fixity set with {0} items", fixitySet1.size()));
		for(Fixity fixity : fixitySet1)
		{
			log.debug(MessageFormat.format("Checking fixity set for a match for {0} ({1})", fixity.getValue(), fixity.getAlgorithm()));
			Fixity fixity2 = null;
			for(Fixity checkFixity : fixitySet2)
			{
				if (checkFixity.getAlgorithm().equals(fixity.getAlgorithm()))
				{
					fixity2 = checkFixity;
				}
			}
			if (fixity2 != null)
			{
				if (fixity.getValue().equals(fixity2.getValue()))
				{
					log.debug("Match found by algorithm and fixities values match");
					matchFound = true;
				}
				else
				{
					log.debug("Match found by algorithm, but fixity values don't match");
					return false;
				}
			}
		}
		if (! matchFound)
		{
			log.debug("No matches were found");
		}
		return matchFound;
	}
		
	public static Set<Fixity> createFixitySet(Fixity fixity)
	{
		Set<Fixity> fixitySet = new HashSet<Fixity>();
		fixitySet.add(fixity);
		return fixitySet;
	}
		
}
