package gov.loc.repository.transfer.ui.controllers;

//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Test;
//import org.testng.annotations.AfterTest;

//import static org.junit.Assert.*;

import java.util.Map;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import org.jvnet.hyperjaxb3.ejb.tests.po.PurchaseOrderType;
import org.jvnet.hyperjaxb3.ejb.tests.po.Items;
import org.jvnet.hyperjaxb3.ejb.tests.po.Items.Item;
import org.jvnet.hyperjaxb3.ejb.tests.po.USAddress;

import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.test.jpa.AbstractJpaTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
 
// ApplicationContext will be loaded from "/base-context.xml" in the root of the classpath
@ContextConfiguration(locations={"/context-core.xml"})
public class ExampleControllerTest extends AbstractJpaTests {
    
	protected static final Log logger = LogFactory.getLog(ExampleControllerTest.class);
	
    public ExampleControllerTest() { }
    
    private IExampleController exampleController;
    
    public void setExampleController(IExampleController exampleController) {
        this.exampleController = exampleController;
    }
    
    @Before public void launchSetup() throws Exception {
        this.logger.debug("Before test ExampleControllerTest");
        setUp();
    }
    
    @Test public void testQuery() throws Exception {
        this.logger.debug("Running test testQuery");
        PurchaseOrderType po = exampleController.fromFile("src/test/resources/po-sample-01.xml");
        exampleController.save(po);
        List results = exampleController.query("from PurchaseOrderType po where po.shipTo.name=?", "Alice Smith");
        assertEquals(1, results.size());
    }

    @Test public void testSave() throws Exception {
        this.logger.debug("Running test testSave");
        exampleController.save(createPurchaseOrder());
    }
    
    @After public void launchTearDown() throws Exception {
        this.logger.debug("After test ExampleControllerTest");
        tearDown();
    }

    protected String[] getConfigLocations() {
      return new String[] { "context-core.xml" };
    }
    
    protected PurchaseOrderType createPurchaseOrder(){
        PurchaseOrderType po = new PurchaseOrderType();
        USAddress shipTo = new USAddress();
        shipTo.setName(     "ShipTo:Name");
        shipTo.setStreet(   "ShipTo:Street");
        shipTo.setCity(     "ShipTo:City");
        shipTo.setState(    "ShipTo:State");
        shipTo.setZip(new BigDecimal(12345));
        po.setShipTo(shipTo);
        
        USAddress billTo = new USAddress();
        billTo.setName(     "BillTo:Name");
        billTo.setStreet(   "BillTo:Street");
        billTo.setCity(     "BillTo:City");
        billTo.setState(    "BillTo:State");
        billTo.setZip(new BigDecimal(54321));
        po.setBillTo(billTo);
        
        po.setComment(
            "This is a Test Comment that should be long enough to make "+
            "someone believe it might be a real comment"
        );
        
        Items items = new Items();
        for (int i=0;i<10;i++){
            Item item = new Item();
            item.setProductName("ProductName:"+Math.random());
            item.setQuantity(1);
            item.setUSPrice(new BigDecimal(2.00));
            item.setComment("Another Comment");
            item.setPartNum("1234567890");
            items.getItem().add(item);   
        }
        po.setItems(items);
        return po;
    }
}
