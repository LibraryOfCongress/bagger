from django.conf.urls.defaults import *
from django.core.urlresolvers import reverse

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    # (r'^deposit/', include('deposit.foo.urls')),

    # Uncomment the admin/doc line below and add 'django.contrib.admindocs' 
    # to INSTALLED_APPS to enable admin documentation:
    # (r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/$', admin.site.root, name="admin_url"),
    (r'^admin/(.*)', admin.site.root),
    #(r'^login/$', 'django.contrib.auth.views.login', {'template_name': 'login.html'}),
    url(r'^login/$', 'deposit.depositapp.views.login', name="login_url"),    
    #url(r'^logout/$', 'django.contrib.auth.views.logout_then_login', {'login_url':reverse('login_url')}, name="logout_url"),
    url(r'^logout/$', 'deposit.depositapp.views.logout', name="logout_url"),
    url(r'^user/(?P<username>\w+)$', 'deposit.depositapp.views.user', name="user_url"),
    url(r'^transfer/create(?P<transfer_type>\w+)$', 'deposit.depositapp.views.create_transfer', name="create_transfer_url"),
    url(r'^transfer/(?P<transfer_id>\d+)$', 'deposit.depositapp.views.transfer', name="transfer_url"),
    url(r'^transfer/$', 'deposit.depositapp.views.list_transfer', name="transfers_url"),
    url(r'^project/(?P<project_id>\d+)$', 'deposit.depositapp.views.project', name="project_url"),
    (r'^.*$', 'deposit.depositapp.views.index'),
)
