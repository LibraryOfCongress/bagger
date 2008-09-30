from deposit.depositapp.models import Project, User, NetworkTransfer, ShipmentTransfer
from django.contrib import admin

admin.site.register((Project,User,NetworkTransfer,ShipmentTransfer))
