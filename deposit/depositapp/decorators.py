from django.http import HttpResponse, HttpResponseRedirect, Http404, HttpResponseBadRequest, HttpResponseForbidden
from django.contrib.auth import REDIRECT_FIELD_NAME
from django.shortcuts import render_to_response
from django.contrib.auth.views import login as base_login, logout_then_login
from django.contrib.auth.models import User
from django.views.generic.create_update import create_object
import deposit.depositapp.models as models
import deposit.depositapp.forms as forms
from django.template import RequestContext
from django.core.urlresolvers import reverse

from django.contrib.auth.decorators import user_passes_test
from django.contrib.auth import REDIRECT_FIELD_NAME
from django.core.urlresolvers import reverse

def login_required(function=None, login_url=None, redirect_field_name=REDIRECT_FIELD_NAME):
    """
    Decorator for views that checks that the user is logged in, redirecting
    to the log-in page if necessary.
    """
    print reverse('depositapp.login_url')
    actual_decorator = user_passes_test(
        lambda u: u.is_authenticated(),
        redirect_field_name=redirect_field_name,
        #login_url=reverse('login_url')
    )
    if function:
        return actual_decorator(function)
    return actual_decorator