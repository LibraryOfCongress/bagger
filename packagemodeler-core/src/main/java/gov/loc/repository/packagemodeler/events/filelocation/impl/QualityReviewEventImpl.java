package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.QualityReviewEvent;

@Entity(name="QualityReviewEvent")
@DiscriminatorValue("qualityreview")
public class QualityReviewEventImpl extends FileLocationEventImpl implements QualityReviewEvent {

}
