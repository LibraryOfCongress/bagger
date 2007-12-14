package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromManifestEvent;

@Entity(name="InventoryFromManifestEvent")
@DiscriminatorValue("inventoryfrommanifest")
public class InventoryFromManifestEventImpl extends FileLocationEventImpl implements InventoryFromManifestEvent
{
}
