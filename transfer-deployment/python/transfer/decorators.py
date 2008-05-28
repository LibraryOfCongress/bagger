import inspect

class project_name(object):
    """ decorator for injecting project_name into class initializer """
    def __init__(self, project_name=""):
        self.project_name = project_name
    def __call__(self, f):
        argspec = inspect.getargspec(f)
        def wrapper(*args, **kwargs):
            obj = args[0]
            setattr(obj, "project_name", self.project_name)
            argvl = zip(argspec[0][1:], args[1:])
            self.copy2attribs(obj, argvl)
            f(*args, **kwargs)
        wrapper.func_doc = f.func_doc
        return wrapper
    def copy2attribs(self, obj, argvl):
        for argn, argv in argvl:
            if isinstance(argn, list):
                self.copy2attribs(obj, zip(argn, argv))
            else: setattr(obj, argn, argv)
