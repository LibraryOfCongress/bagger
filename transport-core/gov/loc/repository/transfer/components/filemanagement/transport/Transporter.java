package gov.loc.repository.transfer.components.filemanagement.transport;

import org.python.core.*;

public class Transporter extends gov.loc.repository.transfer.components.filemanagement.transport.AbstractTransporter implements org.python.core.PyProxy, org.python.core.ClassDictInit {
    static String[] jpy$mainProperties = new String[] {"python.modules.builtin", "exceptions:org.python.core.exceptions"};
    static String[] jpy$proxyProperties = new String[] {"python.modules.builtin", "exceptions:org.python.core.exceptions", "python.options.showJavaExceptions", "true"};
    static String[] jpy$packages = new String[] {"gov.loc.repository.transfer.components.filemanagement.transport", null, "java", null, "org.apache.tools.ant.taskdefs.optional.ssh", null};
    
    public static class _PyInner extends PyFunctionTable implements PyRunnable {
        private static PyObject s$0;
        private static PyObject s$1;
        private static PyObject s$2;
        private static PyObject s$3;
        private static PyObject s$4;
        private static PyFunctionTable funcTable;
        private static PyCode c$0___init__;
        private static PyCode c$1_transport;
        private static PyCode c$2_Transporter;
        private static PyCode c$3_main;
        private static void initConstants() {
            s$0 = Py.newString(" \012    CLASSPATH: /usr/share/jython2.2.1/jython.jar, /usr/share/java/jsch-0.1.28.jar, \012               /usr/share/ant/lib/ant-jsch.jar, /usr/share/ant/lib/ant.jar\012\012    Compile with: jythonc -idp gov.loc.repository.transfer.components.filemanagement.transport -w . Transporter.py\012");
            s$1 = Py.newString("@sig public void transport(String from_uri, String to_uri)");
            s$2 = Py.newString("");
            s$3 = Py.newString("%s -> %s");
            s$4 = Py.newString("/home/mjg/workspace/Biter/jython/Transporter.py");
            funcTable = new _PyInner();
            c$0___init__ = Py.newCode(2, new String[] {"self", "keyfile"}, "/home/mjg/workspace/Biter/jython/Transporter.py", "__init__", false, false, funcTable, 0, null, null, 0, 17);
            c$1_transport = Py.newCode(3, new String[] {"self", "from_uri", "to_uri", "scp"}, "/home/mjg/workspace/Biter/jython/Transporter.py", "transport", false, false, funcTable, 1, null, null, 0, 17);
            c$2_Transporter = Py.newCode(0, new String[] {}, "/home/mjg/workspace/Biter/jython/Transporter.py", "Transporter", false, false, funcTable, 2, null, null, 0, 16);
            c$3_main = Py.newCode(0, new String[] {}, "/home/mjg/workspace/Biter/jython/Transporter.py", "main", false, false, funcTable, 3, null, null, 0, 16);
        }
        
        
        public PyCode getMain() {
            if (c$3_main == null) _PyInner.initConstants();
            return c$3_main;
        }
        
        public PyObject call_function(int index, PyFrame frame) {
            switch (index){
                case 0:
                return _PyInner.__init__$1(frame);
                case 1:
                return _PyInner.transport$2(frame);
                case 2:
                return _PyInner.Transporter$3(frame);
                case 3:
                return _PyInner.main$4(frame);
                default:
                return null;
            }
        }
        
        private static PyObject __init__$1(PyFrame frame) {
            frame.getlocal(0).__setattr__("keyfile", frame.getlocal(1));
            return Py.None;
        }
        
        private static PyObject transport$2(PyFrame frame) {
            /* @sig public void transport(String from_uri, String to_uri) */
            frame.setlocal(3, frame.getglobal("ssh").__getattr__("Scp").__call__());
            frame.getlocal(3).invoke("setKeyfile", frame.getlocal(0).__getattr__("keyfile"));
            frame.getlocal(3).invoke("setPassphrase", s$2);
            frame.getlocal(3).invoke("setTrust", frame.getglobal("True"));
            frame.getlocal(3).invoke("setProject", frame.getglobal("Project").__call__());
            frame.getlocal(3).invoke("setFile", frame.getlocal(1));
            frame.getlocal(3).invoke("setTodir", frame.getlocal(2));
            frame.getlocal(3).invoke("execute");
            Py.println(Py.None, s$3._mod(new PyTuple(new PyObject[] {frame.getlocal(1), frame.getlocal(2)})));
            return Py.None;
        }
        
        private static PyObject Transporter$3(PyFrame frame) {
            frame.setlocal("__init__", new PyFunction(frame.f_globals, new PyObject[] {}, c$0___init__));
            frame.setlocal("transport", new PyFunction(frame.f_globals, new PyObject[] {}, c$1_transport));
            return frame.getf_locals();
        }
        
        private static PyObject main$4(PyFrame frame) {
            frame.setglobal("__file__", s$4);
            
            // Temporary Variables
            PyObject[] t$0$PyObject__;
            
            // Code
            frame.setlocal("java", org.python.core.imp.importOne("java", frame));
            t$0$PyObject__ = org.python.core.imp.importFrom("gov.loc.repository.transfer.components.filemanagement.transport", new String[] {"AbstractTransporter"}, frame);
            frame.setlocal("AbstractTransporter", t$0$PyObject__[0]);
            t$0$PyObject__ = null;
            t$0$PyObject__ = org.python.core.imp.importFrom("org.apache.tools.ant", new String[] {"Project"}, frame);
            frame.setlocal("Project", t$0$PyObject__[0]);
            t$0$PyObject__ = null;
            t$0$PyObject__ = org.python.core.imp.importFrom("org.apache.tools.ant.taskdefs.optional", new String[] {"ssh"}, frame);
            frame.setlocal("ssh", t$0$PyObject__[0]);
            t$0$PyObject__ = null;
            /*  
                CLASSPATH: /usr/share/jython2.2.1/jython.jar, /usr/share/java/jsch-0.1.28.jar, 
                           /usr/share/ant/lib/ant-jsch.jar, /usr/share/ant/lib/ant.jar
            
                Compile with: jythonc -idp gov.loc.repository.transfer.components.filemanagement.transport -w . Transporter.py
             */
            frame.setlocal("Transporter", Py.makeClass("Transporter", new PyObject[] {frame.getname("AbstractTransporter")}, c$2_Transporter, null, Transporter.class));
            return Py.None;
        }
        
    }
    public static void moduleDictInit(PyObject dict) {
        dict.__setitem__("__name__", new PyString("Transporter"));
        Py.runCode(new _PyInner().getMain(), dict, dict);
    }
    
    public static void main(String[] args) throws java.lang.Exception {
        String[] newargs = new String[args.length+1];
        newargs[0] = "Transporter";
        java.lang.System.arraycopy(args, 0, newargs, 1, args.length);
        Py.runMain(gov.loc.repository.transfer.components.filemanagement.transport.Transporter._PyInner.class, newargs, Transporter.jpy$packages, Transporter.jpy$mainProperties, "gov.loc.repository.transfer.components.filemanagement.transport", new String[] {"Transporter"});
    }
    
    public void super__transport(java.lang.String from_uri, java.lang.String to_uri) {
        super.transport(from_uri, to_uri);
    }
    
    public void transport(java.lang.String from_uri, java.lang.String to_uri) {
        PyObject inst = Py.jfindattr(this, "transport");
        if (inst != null) inst._jcall(new Object[] {from_uri, to_uri});
        else super.transport(from_uri, to_uri);
    }
    
    public java.lang.Object clone() throws java.lang.CloneNotSupportedException {
        return super.clone();
    }
    
    public void finalize() throws java.lang.Throwable {
        super.finalize();
    }
    
    public Transporter(java.lang.String arg0) {
        super(arg0);
        __initProxy__(new Object[] {arg0});
    }
    
    private PyInstance __proxy;
    public void _setPyInstance(PyInstance inst) {
        __proxy = inst;
    }
    
    public PyInstance _getPyInstance() {
        return __proxy;
    }
    
    private PySystemState __sysstate;
    public void _setPySystemState(PySystemState inst) {
        __sysstate = inst;
    }
    
    public PySystemState _getPySystemState() {
        return __sysstate;
    }
    
    public void __initProxy__(Object[] args) {
        Py.initProxy(this, "gov.loc.repository.transfer.components.filemanagement.transport.Transporter", "Transporter", args, Transporter.jpy$packages, Transporter.jpy$proxyProperties, "gov.loc.repository.transfer.components.filemanagement.transport", new String[] {"Transporter"});
    }
    
    static public void classDictInit(PyObject dict) {
        dict.__setitem__("__supernames__", Py.java2py(new String[] {"super__transport", "finalize", "clone"}));
    }
    
}
