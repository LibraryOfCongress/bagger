package gov.loc.repository.packagemodeler.events.filelocation.impl;

import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;

import javax.persistence.*;

@Entity(name="InventoryFromFilesOnDiskEvent")
@DiscriminatorValue("inventoryfromfilesondisk")
public class InventoryFromFilesOnDiskEventImpl extends FileLocationEventImpl implements InventoryFromFilesOnDiskEvent
{
}
