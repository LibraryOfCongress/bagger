package gov.loc.repository.transfer.ui.models;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Permission<Long> {
    
   protected String name;
   protected String actions;
   
   public String getName(){
       return this.name;
   }
   public void setName(String name){
       this.name = name;
   }
   
   public String getActions(){
       return this.actions;
   }
   public void setActions(String actions){
       this.actions = actions;
   }
}
