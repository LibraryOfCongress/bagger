package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;

@Entity(name="VerifyAgainstManifestEvent")
@DiscriminatorValue("verifyagainstmanifest")
public class VerifyAgainstManifestEventImpl extends FileLocationEventImpl implements VerifyAgainstManifestEvent
{
}
