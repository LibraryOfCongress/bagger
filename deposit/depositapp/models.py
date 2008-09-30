from django.db import models
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse

class Project(models.Model):
    NETWORK_TRANSFER_TYPES = (
        ('NetworkTransfer', 'network transfer'),
        ('NdnpNetworkTransfer', 'ndnp network transfer'),
    )
    SHIPMENT_TRANSFER_TYPES = (
        ('ShipmentTransfer', 'shipment transfer'),
        ('NdnpShipmentTransfer', 'ndnp shipment transfer'),
    )
    
    name = models.CharField(max_length=255)
    contact_name = models.CharField(max_length=255)
    contact_phone = models.CharField(max_length=10)
    contact_email = models.EmailField()
    shipping_address = models.CharField(max_length=255)
    network_transfer_type = models.CharField(max_length=50, blank=True, null=True, choices=NETWORK_TRANSFER_TYPES)
    shipment_transfer_type = models.CharField(max_length=50, blank=True, null=True, choices=SHIPMENT_TRANSFER_TYPES)
    
    def __unicode__(self):
        return u'%s' % (self.name)

    def get_absolute_url(self):
        return reverse('project_url', args=[self.id])
    
class User(models.Model):
    user = models.ForeignKey(User, unique=True)
    organization = models.CharField(max_length=150)
    address = models.CharField(max_length=255)
    phone = models.CharField(max_length=10)
    projects = models.ManyToManyField(Project)    

    def __unicode__(self):
        return u'%s' % (self.user.username)

    def get_absolute_url(self):
        return reverse('user_url', args=[self.user.username])


class Transfer(models.Model):
    #Supplied list of packages included in transfer.
    #This is only a hint and may differ from what is discovered when the delivered packages are examined.
    package_ids = models.CharField(max_length=255, help_text="List of packages included in the transfer.")
    user = models.ForeignKey('User')
    project = models.ForeignKey('Project')
    transfer_type = models.CharField(max_length=50, editable=False)
    create_timestamp = models.DateTimeField(auto_now_add=True, editable=False)

    def __unicode__(self):
        return u'%s %s' % (self.transfer_type, self.id)    
        
    def get_absolute_url(self):
        return reverse('transfer_url', args=[self.id])

class Ndnp(models.Model):
    lccns = models.CharField(max_length=255, help_text="List of LCCNs included in the transfer.")

class NetworkTransfer(Transfer):
    location = models.URLField(verify_exists=False, help_text="URL from which the package is to be retrieved.")
    username = models.CharField(max_length=100, null=True, blank=True, help_text="Optional. Username needed to access the package.")
    password = models.CharField(max_length=100, null=True, blank=True, help_text="Optional. Password needed to access the package.")
    estimated_size = models.IntegerField(help_text="Estimated size of the complete package in GB.")

    def __init__(self, *args, **kwargs):
        apply(super(NetworkTransfer,self).__init__,args, kwargs)
        self.transfer_type = self.__class__.__name__

        
class NdnpNetworkTransfer(NetworkTransfer, Ndnp):
    def __init__(self, *args, **kwargs):
        apply(super(NdnpNetworkTransfer,self).__init__,args, kwargs)
        self.transfer_type = self.__class__.__name__

class ShipmentTransfer(Transfer):
    MEDIA_TYPES = (
        ('EXTERNAL_HARDDRIVE', 'hard drive'),
        ('DVD', 'dvd'),
        ('CD','cd')
    )
    ship_date = models.DateField(help_text="Date the package is shipped or will be shipped.")
    ship_method = models.CharField(max_length=100, help_text="The shipping service for the shipment, e.g., FedEx.")
    ship_tracking_number = models.CharField(max_length=150, help_text="The tracking number that identifies the shipment with the shipping service.")
    media_type = models.CharField(max_length=20, choices=MEDIA_TYPES, help_text="The type of media that is being shipped.")
    #List of identifiers for the media, e.g., drive names or serial numbers
    media_identifiers = models.CharField(max_length=255, null=True, blank=True, help_text="Optional.  Identifiers for the media, e.g., a list of hard drive serial numbers.")
    number_media_shipped = models.IntegerField(help_text="The number of media being shipped.")
    #Additional 
    addl_equipment = models.CharField(max_length=255, null=True, blank=True, help_text="Optional.  Any additional equipment being shipped, e.g., cables.")

    def __init__(self, *args, **kwargs):
        apply(super(ShipmentTransfer,self).__init__,args, kwargs)
        self.transfer_type = self.__class__.__name__

class NdnpShipmentTransfer(ShipmentTransfer, Ndnp):
    def __init__(self, *args, **kwargs):
        apply(super(NdnpShipmentTransfer,self).__init__,args, kwargs)
        self.transfer_type = self.__class__.__name__
