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

def index(request):
    if request.user.is_authenticated():
        return HttpResponseRedirect(reverse('user_url', args=[request.user.username]))
    return HttpResponseRedirect(reverse('login_url'))    

def login(request, redirect_field_name=REDIRECT_FIELD_NAME):
    if request.method == 'POST':
        request.POST = request.POST.__copy__()
        request.POST[redirect_field_name] = reverse('user_url', args=[request.POST['username']])
    return base_login(request, "login.html",  redirect_field_name)

def logout(request):
    return logout_then_login(request, login_url=reverse('login_url'))

def user(request, username):
    try:
        user = User.objects.get(username=username)
    except User.DoesNotExist:
        raise Http404
    try:
        user_profile = user.get_profile()
    except models.User.DoesNotExist:
        user_profile = None
    if request.user.is_authenticated() and request.user.username == username:
        is_user = True
    else:
        is_user = False
    
    return render_to_response('user.html', {'user_profile': user_profile, 'is_user':is_user, 'projects':models.Project.objects}, context_instance=RequestContext(request))

def transfer(request, transfer_id):
    if not request.user.is_authenticated():
        return HttpResponseForbidden()
    if request.method == 'POST':
        return HttpResponseNotAllowed()
    try:
        transfer = models.Transfer.objects.get(id=transfer_id)
    except Transfer.DoesNotExist:
       raise Http404
    transfer_class = getattr(models, transfer.transfer_type)
    transfer_sub = transfer_class.objects.get(id=transfer_id)
    template_name = "%s.html" % transfer.transfer_type.lower()
    return render_to_response(template_name, {'transfer':transfer_sub}, context_instance=RequestContext(request))    

def project(request, project_id):
    if request.method == 'POST':
        return HttpResponseNotAllowed()
    try:
        project = models.Project.objects.get(id=project_id)
    except models.Project.DoesNotExist:
       raise Http404
    return render_to_response("project.html", {'project':project}, context_instance=RequestContext(request))    

def create_transfer(request, transfer_type):
    if not request.user.is_authenticated():
        return HttpResponseForbidden()
    form_class = getattr(forms, transfer_type + "Form")
    template_name = "transfer_form.html"
    if request.method == 'GET':
        project_id = request.GET['project_id']         
    if request.method == 'POST':
        project_id = request.POST['project_id']
        form = form_class(request.POST, request.FILES)
        if form.is_valid():
            new_object = form.save(commit=False)                     
            new_object.project = models.Project.objects.get(id=project_id)
            new_object.user = models.User.objects.filter(user__pk=request.user.pk)[0]
            new_object.save()
            request.user.message_set.create(message="The transfer was registered.  A confirmation has been sent to %s and %s." % (new_object.user.user.email, new_object.project.contact_email))
            print request.user.message_set
            return HttpResponseRedirect(new_object.get_absolute_url())
    else:
        form = form_class()

    # Create the template, context, response
    return render_to_response(template_name, {'form':form, 'project_id':project_id, 'transfer_type':transfer_type}, context_instance=RequestContext(request))

def list_transfer(request):
    if not request.user.is_authenticated():
        return HttpResponseForbidden()
    if request.method == 'POST':
        return HttpResponseNotAllowed()
    if not request.GET.has_key('username') and not request.GET.has_key('project_id'):
        return HttpResponseBadRequest()
    if request.GET.has_key('username'):
        #Make sure user has user_id
        if request.user.username != request.GET['username']:
            return HttpResponseForbidden()
        transfers = models.Transfer.objects.filter(user__user__username=request.GET['username'])
    elif request.GET.has_key('project_id'):
        #Make sure that user is associated with project
        if not request.user.is_staff and not request.user.is_superuser and len(request.user.get_profile().projects.filter(id=request.GET['project_id'])) == 0:
            return HttpResponseForbidden()
        transfers = models.Transfer.objects.filter(project__id=request.GET['project_id'])        
    return render_to_response("transfer_list.html", {'transfers':transfers}, context_instance=RequestContext(request))