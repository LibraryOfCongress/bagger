from django.forms import ModelForm, URLField
import deposit.depositapp.models as models

class NetworkTransferForm(ModelForm):
    class Meta:
        model = models.NetworkTransfer
        exclude = ('project','user')
        
class ShipmentTransferForm(ModelForm):
    class Meta:
        model = models.ShipmentTransfer
        exclude = ('project','user')

class NdnpNetworkTransferForm(ModelForm):
    class Meta:
        model = models.NdnpNetworkTransfer
        exclude = ('project','user')
        
class NdnpShipmentTransferForm(ModelForm):
    class Meta:
        model = models.NdnpShipmentTransfer
        exclude = ('project','user')
