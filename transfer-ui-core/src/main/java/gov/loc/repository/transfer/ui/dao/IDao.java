package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Base;
import java.io.Serializable;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Chris Thatcher
 */
//@Transactional(readOnly=true)
public interface IDao<T , PK extends Serializable>{

    public List<T> findAll();

    public T find(PK uuid);

    //@Transactional(readOnly=false)
    public void save(T entity);

    //@Transactional(readOnly=false)
    public T merge(T entity);

    //@Transactional(readOnly=false)
    public void remove(T entity);
    
    public Class<T> getType();
    
    //public List query(String queryString, final Object... params);
}
