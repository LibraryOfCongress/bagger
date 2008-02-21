(function(){

Claypool={
    $:function(id){
        return Claypool.Application.getApplicationContext().get(id);
    },
    guid:(Math.random()*1000000000000)|0,//casts as an int
    createGUID: function(){
        return ++Claypool.guid;
    },
    SimpleCachingStrategy$Class:{
        cache:null,
        size:null,
        constructor: function(options){
            Claypool.extend(true, this, Claypool.SimpleCachingStrategy$Class);
            Claypool.extend(true, this, options);
            this.logger = new Claypool.Logging.NullLogger();
            this.clear();
            return this;
        },
        clear: function(){
            this.logger.debug("Clearing Cache.");
    		this.cache = null;
    		this.cache = {};
    		this.size = 0;
    	},
    	add: function(id, object){
	        this.logger.debug("Adding To Cache: %s", id);
		    if ( !this.cache[id] ){
    			this.cache[id] = object;
    			this.size++;
    			return id;
    		}
    		return null;
    	},
    	remove: function(id){
    	    this.logger.debug("Removing From Cache id: %s", id);
    	    if(this.find(id)){
    	        return (delete this.cache[id])?--this.size:-1; 
    	    }
    	},
    	find: function(id){
    	    this.logger.debug("Searching Cache for id: %s", id);
    		return this.cache[id] || null;
    	}
    },
    Router$Class:{//TODO: a good place to use jQuery.collections
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.SimpleCachingStrategy(options));
            Claypool.extend(true, this, Claypool.Router$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Router");
        },
        /**the pattern map is any object, the pattern key is the name of 
        the objects property which is treated as a string to be compiled to
        a regular expression, The pattern key can actually be a '|' seperated
        set of strings.  the first one that is a property of the map will be used*/
        compile: function(patternMap, patternKey){
            this.logger.debug("compiling patterns for match strategies");
            var pattern, routable;
            var i, j; 
            patternKey = patternKey.split('|');//supports flexible pattern keys
            for(i=0;i<patternMap.length;i++){
                for( j = 0; j<patternKey.length;j++){
                    pattern = patternMap[i][patternKey[j]];
                    if(pattern){
                        this.logger.debug("Compiling \n\tpattern: %s for \n\ttarget.", pattern);
                        /**pattern might be used more than once so we need a unique key to store the route*/
                        this.add(String(Claypool.createGUID()) , {
                            pattern:pattern, 
                            payload:patternMap[i]
                        });
                    }
                }
            }
            return this;
        },
        first: function(string){
            this.logger.debug("Using strategy 'matchFirst'");
            var regexp, route, id;
            for(id in this.cache){
                route = this.find(id);
                this.logger.debug("checking pattern %s for string %s", route.pattern, string);
                regexp = new RegExp(route.pattern);
                if(regexp.test(string)){
                    this.logger.debug("found match for \n\tpattern: %s \n\ttarget : %s ", 
                        route.pattern, route.payload.controller);
                    return [route];
                }
            }
            this.logger.debug("found no match for \n\tpattern: %s", string);
            return [];
        },
        all: function(string){
            this.logger.debug("Using strategy 'matchAll'");
            var routeList = [];
            var regexp, route, id;
            for(id in this.cache){
                route = this.find(id);
                regexp = new RegExp(route.pattern);
                this.logger.debug("checking pattern: %s for string %s", route.pattern, string);
                if(regexp.test(string)){
                    this.logger.debug("found match for \n\tpattern: %s \n\ttarget : %s ", 
                        route.pattern, route.payload.controller);
                    routeList.push(route);
                }
            }
            if(routeList.length===0){this.logger.debug("found no match for \n\tpattern: %s", string);}
            return routeList;
        }
    },
    AbstractContext$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.SimpleCachingStrategy(options));
            Claypool.extend(true, this, Claypool.AbstractContext$Class);
            Claypool.extend(true, this, options);
            this.logger = new Claypool.Logging.NullLogger();
        },
        get: function(id){
            throw new Error("Method not implemented");
        },
        put: function(id, object){
            throw new Error("Method not implemented");
        }
    },
    ContextContributor$Class:{
        constructor: function(){
            Claypool.extend(true, this, new Claypool.AbstractContext());
            Claypool.extend(true, this, Claypool.ContextContributor$Class);
            this.logger = Claypool.Logging.getLogger("Claypool.ContextContributor");
            return this;
        },
        registerContext: function(id){
            throw new Error("Method not implemented");
        }
    },
    BaseFactory$Class:{
        //By default the factories are configured programatically, using setConfiguration,
        //however all the wiring is available for separating it into a data format like
        //json or xml and retreiving via ajax (though not asynchronously)
        //Factories also manage the cache of objects they create for fast retreival
        //by id, thus the cache is a simple map implementation.
        configuration:null,
        configurationUrl:"application.context.js",
        configurationType:"json",//or xml
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.SimpleCachingStrategy());
            Claypool.extend(true, this, Claypool.BaseFactory$Class);
            Claypool.extend(true, this, options);
            this.logger = new Claypool.Logging.NullLogger();
            return this;
        },
        loadConfiguration: function(url){
            this.configurationUrl = url||this.configurationUrl;
            this.logger.info("Attempting to load configuration from: %s", this.configurationUrl);
            //a non async call because we need to configure the loggers
            //with this info before they are called!
            _this = this;
            try{
                jQuery.ajax({
                    type: "Get",
        			url: this.configurationUrl,
        			async: false,
        			data:{},
        			dataType: "json",
        	        success: function(json){
        	            _this.setConfiguration(json&&json?json:{});
        	        }
        	    });
    	    }catch(e){
    	        this.logger.exception(e);
                throw new Claypool.ConfigurationException(e);
    	    }
            return true;
        },
        getConfiguration: function(){
            if( !this.configuration ){
                //First look for an object name Claypool.Configuration
                this.logger.warn("Configuration has not been set explicitly, introspecting to see if we can discover it.");
                try{
                    if(Claypool.Configuration){
                        this.logger.info("Found Claypool.Configuration");
                        this.configuration = Claypool.Configuration;
                    }else{
                        //it's not specified in js code so look for it remotely
                        this.loadConfiguration();
                    }
                }catch(e){
                    this.logger.exception(e);
                    throw new Claypool.ConfigurationError(e);
                }
            }
            return this.configuration;
        },
    	setConfiguration: function(configuration){
    	    this.logger.info("Setting configuration");
            this.configuration = configuration;
            if(!Claypool.Configuration){
                Claypool.Configuration = configuration;
            }
            return 1;
        },
        updateConfigurationCache: function(){
            throw new Claypool.MethodNotImplementedError();
        },
        create: function(){
            throw new Claypool.MethodNotImplementedError();
        }
    },
    Error$Class:{
        constructor: function(e, options){
            Claypool.extend(true, this, e||new Error());
            this.name = options.name||"Claypool.Error" + " > " + this.name||"UnknownError";
            this.message = options.name||"No Message Provided"+
                            "\n Nested exception is:\n\t" + this.message||"UnknownError";
        }
    },
    /**@exception*/
    ConfigurationError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.ConfigurationError",
                message: "An error occured trying to locate or load the system configuration."
            }));
        }
    },
    /**@exception*/
    MethodNotImplementedError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.MethodNotImplementedError",
                message: "Method not implemented."
            }));
        }
    }
};
/**@constructorAlias*/
Claypool.SimpleCachingStrategy  = Claypool.SimpleCachingStrategy$Class.constructor;
/**@constructorAlias*/
Claypool.Router                 = Claypool.Router$Class.constructor;
/**@constructorAlias*/
Claypool.AbstractContext        = Claypool.AbstractContext$Class.constructor;
/**@constructorAlias*/
Claypool.ContextContributor     = Claypool.ContextContributor$Class.constructor;
/**@constructorAlias*/
Claypool.BaseFactory            = Claypool.BaseFactory$Class.constructor;
/**@constructorAlias*/
Claypool.Error                  = Claypool.Error$Class.constructor;

//Exception Classes
/**@constructorAlias*/
Claypool.ConfigurationError         = Claypool.ConfigurationError$Class.constructor;
/**@constructorAlias*/
Claypool.MethodNotImplementedError  = Claypool.MethodNotImplementedError$Class.constructor;/**
*   The providers are not used yet but their purpose is to remove
*   the dependency of the classes inside claypool.  The goal is to
*   encourage developers to code to interface contracts, encourage
*   healthy competition, and promote interoperability.
*/
Claypool.extend = jQuery.extend;
Claypool.isFunction = jQuery.isFunction;

Claypool.Logging = {
    //Static Closure Method (uses a singleton pattern)
    loggerFactory:null,
    getLogger: function(category){
        if(!Claypool.Logging.loggerFactory){
            Claypool.Logging.loggerFactory = new Claypool.Logging.LoggerFactory();
            Claypool.Logging.loggerFactory.updateConfigurationCache();
        }
        return Claypool.Logging.loggerFactory.getLogger(category);
    },
    Level:{
        DEBUG:0,
        INFO:1,
        WARN:2,
        ERROR:3,
        NONE:4
    },
    LoggerFactory$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.BaseFactory());
            Claypool.extend(true, this, Claypool.Logging.LoggerFactory$Class);
            Claypool.extend(true, this, options);
            //The LogFactory is unique in that it will create its own logger
            //so that it's events can be logged to console or screen in a
            //standard way.
            this.logger = new Claypool.Logging.Logger({
                category:"Claypool.Logging.LoggerFactory",
                level:"INFO",
                appender:"Claypool.Logging.ConsoleAppender"
            });
            return this;
        },
        getLogger: function(category){
            var categoryParts;
            var subcategory;
            var loggerConf;
            var rootLoggerConf;
            if(!this.configuration){
                this.logger.info("Claypool Logging was not initalized correctly.  Logging will not occur unless initialized.");
                return new Claypool.Logging.NullLogger();
            }else{
                //Find the closest configured category
                categoryParts = category.split(".");
                for(i=0;i<categoryParts.length;i++){
                    subcategory = categoryParts.slice(0,categoryParts.length-i).join(".");
                    loggerConf = this.find(subcategory);
                    if(loggerConf !== null){
                        //The level is set by the closest subcategory, but we still want the 
                        //full category to display when we log the messages
                        loggerConf.category = category;
                        return new Claypool.Logging.Logger( loggerConf );
                    }
                }
                //try the special 'root' category
                rootLoggerConf = this.find('root');
                if(rootLoggerConf !== null){
                    //The level is set by the closest subcategory, but we still want the 
                    //full category to display when we log the messages
                    rootLoggerConf.category = category;
                    return new Claypool.Logging.Logger(rootLoggerConf);
                }
            }
            //No matching category found
            this.logger.warn("No Matching category: %s Please configure a root logger.", category);
            return new Claypool.Logging.NullLogger();
        },
        updateConfigurationCache: function(){
            var loggingConfiguration;
            var logconf;
            try{
                this.logger.info("Configuring Claypool Logging");
                this.clear();
                loggingConfiguration = this.getConfiguration().logging;
                for(i=0;i<loggingConfiguration.length;i++){
                    try{
                        logconf = loggingConfiguration[i];
                        this.add( logconf.category, logconf );
                    }catch(e){
                        this.logger.exception(e);
                        return false;
                    }
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.Logging.ConfigurationError(e);
            }
            return true;
        }
    },
    NullLogger$Class:{
        constructor: function(){
            var nullFunction=function(){};
            Claypool.extend(this,  {
                debug:nullFunction,
                info:nullFunction,
                warn:nullFunction,
                error:nullFunction,
                exception:nullFunction
            });
            return this;
        }
    },
    Logger$Class:{
        category:"root",
        level:null,
        constructor: function(options){
            try{
                Claypool.extend(true, this, Claypool.Logging.Logger$Class);
                Claypool.extend(true, this, options);
                this.attach(options);
                return this;
            }catch(e){
                return new Claypool.Logging.NullLogger();
            }
        },
        attach: function(options){
            try{
                this.level = Claypool.Logging.Level[
                    this.level?this.level:"NONE"
                ];
                //TODO: allow for appender extension, eg multiple appenders and custom appenders
                this.appender = (this.appender=='Claypool.Logging.ConsoleAppender')?
                    new Claypool.Logging.ConsoleAppender(options):
                    new Claypool.Logging.SysOutAppender(options);
            }catch(e){
                /*Do Nothing*/
            } 
        },
        debug: function(){
            if(this.level<=Claypool.Logging.Level.DEBUG){
              this.appender.debug(this.category,arguments);  
              return 1;
            }return 0;
        },
        info: function(){
            if(this.level<=Claypool.Logging.Level.INFO){
              this.appender.info(this.category,arguments);  
              return 1;
            }return 0;
        },
        warn: function(){
            if(this.level<=Claypool.Logging.Level.WARN){
              this.appender.warn(this.category,arguments);  
              return 1;
            }return 0;
        },
        error: function(){
            if(this.level<=Claypool.Logging.Level.ERROR){
              this.appender.error(this.category,arguments);  
              return 1;
            }return 0;
        },
        exception: function(e){
            if(e){
              this.appender.exception(this.category,e); 
              return 1;
            }return 0;
        }
    },
    AbstractAppender$Class:{
        formatter:null,
        constructor: function(options){
            Claypool.extend(this, Claypool.Logging.AbstractAppender$Class);
            Claypool.extend(true, this, options);
            return this;
        },
        debug: function(category,message){return;},
        info: function(category,message){return;},
        warn: function(category,message){return;},
        error: function(category,message){return;},
        exception: function(category, e){return;}
    },
    ConsoleAppender$Class:{
        constructor: function(options){
            try{
                if(window&&window.console&&window.console.log){
                    Claypool.extend(true, this, Claypool.Logging.ConsoleAppender$Class);
                    Claypool.extend(true, this, options);
                    this.formatter = new Claypool.Logging.FireBugFormatter(options);
                    return this;
                }
            }catch(e){
                /**Since the console isn't available, see if print() is and fall back to it**/
            }
            Claypool.extend(true,this, Claypool.Logging.SysOutAppender(options));
        },
        getDateString: function(){
            return " ["+ new Date().toUTCString() +"] ";
        },
        debug: function(category,message){
            console.log.apply(console, this.formatter.format(category + " DEBUG: "+ this.getDateString(),  message));
        },
        info: function(category,message){
            console.info.apply(console, this.formatter.format(category + " INFO: "+ this.getDateString(),  message));
        },
        warn: function(category,message){
              console.warn.apply(console, this.formatter.format(category + " WARN: "+ this.getDateString(), message)); 
        },
        error: function(category,message){
              console.error.apply(console, this.formatter.format(category + " ERROR: "+ this.getDateString(), message));  
        },
        exception: function(category,e){
              console.error.apply(console, this.formatter.format(category + " EXCEPTION: "+ this.getDateString(), e.message?[e.message]:[])); 
              console.trace();
        }
    },
    FireBugFormatter$Class:{
        constructor: function(options){
            Claypool.extend(true, this, Claypool.Logging.FireBugFormatter$Class);
            Claypool.extend(true, this, options);
        },
        format: function(msgPrefix, objects){
            objects = (objects&&objects.length&&(objects.length>0))?objects:[];
            var msgFormat = (objects.length > 0)?objects[0]:null;
            if (typeof(msgFormat) != "string"){
                objects.unshift(msgPrefix);
            }else{
                objects[0] = msgPrefix + msgFormat;
            }
            return objects;
        }
    },
    SysOutAppender$Class:{
        constructor: function(options){
            /**This function is intentionally written to throw an error when called*/
            var rhinoCheck = function(){ var isRhino = null;isRhino.toString();};
            try{
                /**This is probably rhino if these are defined*/
                if(jQuery.isFunction(print) && jQuery.isFunction(load) ){
                    try{
                        rhinoCheck();
                    }catch(caught){/**definitely rhino if this is true*/
                        if(caught.rhinoException){
                            Claypool.extend(true, this, Claypool.Logging.SysOutAppender$Class);
                            Claypool.extend(true, this, options);
                            this.formatter = new Claypool.Logging.DefaultFormatter(options);
                            return this;
                        }
                    }
                }
            }catch(e){/**Print isnt available*/}
            Claypool.extend(true, this, new Claypool.Logging.AbstractAppender(options));
        },
        getDateString: function(){
            return " ["+ new Date().toUTCString() +"] ";
        },
        debug: function(category,message){
            print(this.formatter.format(" DEBUG: " +this.getDateString() + "{"+category+"} ",  message)); 
        },
        info: function(category,message){
            print(this.formatter.format(" INFO:  " +this.getDateString() + "{"+category+"} " ,  message));  
        },
        warn: function(category,message){
            print(this.formatter.format(" WARN:  " +this.getDateString() + "{"+category+"} " ,  message));  
        },
        error: function(category,message){
            print(this.formatter.format(" ERROR: " + this.getDateString() + "{"+category+"} " , message));  
        },
        exception: function(category,e){
            var msg = e&&e.rhinoException?"\n\t"      + e.rhinoException.message +
                "\tcolumn: "  + e.rhinoException.columnNumber() + 
                "\tline: "  + e.rhinoException.lineNumber()  : "UNKNOWN RUNTIME ERROR";
            print(this.formatter.format(" EXCEPTION: " + this.getDateString() + "{"+category+"} " ,  msg ));
        }
    },
    DefaultFormatter$Class:{
        parseFormatRegExp:/((^%|[^\\]%)(\d+)?(\.)([a-zA-Z]))|((^%|[^\\]%)([a-zA-Z]))/,
        functionRenameRegExp:/function ?(.*?)\(/,
        objectRenameRegExp:/\[object (.*?)\]/,
        constructor: function(options){
            Claypool.extend(true, this, Claypool.Logging.DefaultFormatter$Class);
            Claypool.extend(true, this, options);
        },
        format: function (msgPrefix, objects){
            var msg = [msgPrefix?msgPrefix:""];
            var format = objects[0];
            var objIndex = 0;
            if (typeof(format) != "string"){
                format = "";
                objIndex = -1;
            }
            var parts = this.parseFormat(format);
            var i;
            for (i = 0; i < parts.length; ++i){
                if (parts[i] && typeof(parts[i]) == "object"){
                    parts[i].appender.call(this,objects[++objIndex], msg);
                }else{
                    this.appendText(parts[i], msg);
                }
            }
            for (i = objIndex+1; i < objects.length; ++i){
                this.appendText(" ", msg);
                if (typeof(objects[i]) == "string"){
                    this.appendText(objects[i], msg);
                }else{
                    this.appendObject(objects[i], msg);
                }
            }
            return msg.join("");
        },
        parseFormat: function(format){
            var parts = [];
            var appenderMap = {s: this.appendText, d: this.appendInteger, i: this.appendInteger, f: this.appendFloat};
            var type;
            var appender;
            var precision;
            var m;
            for (m = this.parseFormatRegExp.exec(format); m; m = this.parseFormatRegExp.exec(format)) {
                type = m[8] ? m[8] : m[5];
                appender = type in appenderMap ? appenderMap[type] : this.appendObject;
                precision = m[3] ? parseInt(m[3], 10) : (m[4] == "." ? -1 : 0);
                parts.push(format.substr(0, m[0][0] == "%" ? m.index : m.index+1));
                parts.push({appender: appender, precision: precision});
                format = format.substr(m.index+m[0].length);
            }
            parts.push(format);
            return parts;
        },
        objectToString: function (object){
            try{ return object+"";}
            catch (e){ return null; }
        },
        appendText: function (object, msg){
            msg.push(this.objectToString(object));
        },
        appendNull: function (object, msg){
            msg.push(this.objectToString(object));
        },
        appendString: function (object, msg){
            msg.push(this.objectToString(object));
        },
        appendInteger: function (object, msg){
            msg.push(this.objectToString(object));
        },
        appendFloat: function (object, msg){
            msg.push(this.objectToString(object));
        },
        appendFunction: function (object, msg){
            var m = this.functionRenameRegExp.exec(this.objectToString(object));
            var name = m ? m[1] : "function";
            msg.push(this.objectToString(name));
        },
        appendObject: function (object, msg){
            try{
                if (object === undefined){
                    this.appendNull("undefined", msg);
                }else if (object === null){
                    this.appendNull("null", msg);
                }else if (typeof object == "string"){
                    this.appendString(object, msg);
                }else if (typeof object == "number"){
                    this.appendInteger(object, msg);
                }else if (typeof object == "function"){
                    this.appendFunction(object, msg);
                }else if (object.nodeType == 1){
                    this.appendSelector(object, msg);
                }else if (typeof object == "object"){
                    this.appendObjectFormatted(object, msg);
                }else{ this.appendText(object, msg); }
            }catch (e){/*Do Nothing*/}
        },
        appendObjectFormatted: function (object, msg){
            var text = this.objectToString(object);
            var m = this.objectRenameRegExp.exec(text);
            msg.push( m ? m[1] : text);
        },
        appendSelector: function (object, msg){
            msg.push(object.nodeName.toLowerCase());
            if (object.id){ msg.push(object.id);}
            if (object.className){ msg.push(object.className);}
            msg.push('</span>');
        },
        appendNode: function (node, msg){
            var attr;
            var i;
            var child;
            if (node.nodeType == 1){
                msg.push('<', node.nodeName.toLowerCase(), '>');
                for (i = 0; i < node.attributes.length; ++i){
                    attr = node.attributes[i];
                    if (!attr.specified){ continue; }
                    msg.push(attr.nodeName.toLowerCase(),'="',attr.nodeValue,'"');
                }
                if (node.firstChild){
                    for (child = node.firstChild; child; child = child.nextSibling){
                        this.appendNode(child, html);
                    }
                    msg.push('</',  node.nodeName.toLowerCase(), '>');
                } else {
                    msg.push('/>');
                }
            }else if (node.nodeType == 3) {
                msg.push( node.nodeValue );
            }
        }
    },
    /**@exception*/
    ConfigurationError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.Logging.ConfigurationError",
                message: "An error occured trying to configure the logging system."
            }));
        }
    }
};
/**@constructorAlias*/
Claypool.Logging.LoggerFactory    = Claypool.Logging.LoggerFactory$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.Logger           = Claypool.Logging.Logger$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.NullLogger       = Claypool.Logging.NullLogger$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.AbstractAppender = Claypool.Logging.AbstractAppender$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.SysOutAppender   = Claypool.Logging.SysOutAppender$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.ConsoleAppender  = Claypool.Logging.ConsoleAppender$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.DefaultFormatter  = Claypool.Logging.DefaultFormatter$Class.constructor; 
/**@constructorAlias*/
Claypool.Logging.FireBugFormatter  = Claypool.Logging.FireBugFormatter$Class.constructor; 

//Exception Classes
/**@constructorAlias*/
Claypool.Logging.ConfigurationError = Claypool.Logging.ConfigurationError$Class.constructor; /**
*   Claypool AOP - 
*
* This code is adopted from the jQuery AOP plugin project.  It was incorporated so it
* could be extended and modified to match the overall javascript style of the rest of Claypool.
* Many thanks to it's author(s), as we rely heavily on the code and learned a lot from
* it's integration into Claypool.
*
* The original header is retained below:
*/

/**
* jQuery AOP - jQuery plugin to add features of aspect-oriented programming (AOP) to jQuery.
* http://jquery-aop.googlecode.com/
*
* Licensed under the MIT license:
* http://www.opensource.org/licenses/mit-license.php
*
* Version: 1.0
*/

Claypool.AOP = {

    /**@class*/
    Container$Class:{
        /**@private*/
        aspectFactory:null,
        /**@constructor*/
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.Application.ApplicationContextContributor(options));
            Claypool.extend(true, this, Claypool.AOP.Container$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.AOP.Container");
            this.logger.debug("Configuring Claypool AOP Container");
            /**Register first so any changes to the container managed objects 
            are immediately accessible to the rest of the application aware
            components*/
            this.registerContext("Claypool.AOP.Container");
            this.aspectFactory = new Claypool.AOP.AspectFactory();
            this.aspectFactory.updateConfigurationCache();
            return this;
        },
        /**@public*/
        //returns all aspects attached to the Class or instance.  If the instance
        //is still sleeping, the proxy aspect is returned.
        get: function(id){//id is #instance or $Class (ie Function)
            var aspect;
            try{
                this.logger.debug("Search for a container managed aspect :%s", id);
                aspect = this.find(id);
                if(aspect===undefined||aspect===null){
                    this.logger.debug("Can't find a container managed aspect :%s", id);
                    aspect = this.aspectFactory.getInstance(id);
                    if(aspect !== null){
                        this.add(id, aspect);
                        return aspect;
                    }
                }else{
                    this.logger.debug("Found container managed instance :%s", id);
                    return aspect;
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.AOP.AspectFactoryError(e);
            }
            return null;
        }
    },
    /**Stores instance configurations and manages instance lifecycles*/
    /**@class*/
    AspectFactory$Class:{
        /**@constructor*/
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.BaseFactory(options));
            Claypool.extend(true, this, Claypool.AOP.AspectFactory$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.AOP.AspectFactory");
            return this;
        },
        /**@public*/
        updateConfigurationCache: function(){
            var aopConfiguration;//entire config
            var aopconf;//unit of config
            var i;
            try{
                this.logger.debug("Configuring Claypool AOP AspectFactory");
                aopConfiguration = this.getConfiguration().aop?this.getConfiguration().aop:[];
                this.logger.debug("AOP Configurations: %d", aopConfiguration.length);
                for(i=0;i<aopConfiguration.length;i++){
                    try{
                        aopconf = aopConfiguration[i];
                        this.add( aopconf.id, aopconf );
                        if(aopconf.target.matches("^#")){
                            //this is an instance so we need to create a proxy aop
                            //to wait till the object is created, and then attach.
                            //This is the perfect class to attach the proxy.:
                        }
                    }catch(e){
                        this.logger.exception(e);
                        return false;
                    }
                }
            }catch(e){
                this.logger.exception(e);
                throw new AOPConfigurationError(e);
            }
            return true;
        },
        getInstance: function(id){//satisfies interface
            return this.getAspect(id);//feels better
        },
        getAspect: function(id){//id is #instance or $Class
            var configuration;
            var aspect;
            try{
                this.logger.debug("Looking for configuration for aspect %s", id);
                configuration = this.find(id);
                if(configuration === undefined || configuration === null){
                    this.logger.warn("No known configuration for aspect %s", id);
                    return null;
                }else{
                    this.logger.debug("Found configuration for instance %s", id);
                    instance = new Claypool.AOP.Aspect(configuration.id, configuration);
                    if(configuration.active&&configuration.selector){
                        this.logger.debug("Attaching contructor to an active selector");
                        _this = this;
                        jQuery(configuration.selector).livequery(function(){
                                _this.createWeave(instance);
                            });
                    }else{
                        this.createWeave(instance);
                    }
                    /**remember this might not be fully initialized yet!*/
                    return instance;
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.AOP.AspectFactoryError(e);
            }
        },
	    /**
    	 * Private weaving function.
    	 */
    	createWeave:function(options)
    	{
    		var aspect;
    		if (advice.type === 'after'){
    			aspect = new Claypool.AOP.After(options);
    		}else if (advice.type === 'before'){
    			aspect = Claypool.AOP.Before(options);
    		}else if (advice.type === 'around') {
    			aspect = Claypool.AOP.Around(options);
    		}
    	}
    },
    AbstractAspect$Class:{//Another good candidate for
        id:null,
        type:null,
        strategy:null,//matchAll|matchFirst
        /**options should include pointcut:{target:'Class or instance', method:'methodName or pattern', advice:function }*/
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.SimpleCachingStrategy(options));
            Claypool.extend(true, this, Claypool.AOP.AbstractAspect$Class);
            Claypool.extend(true, this, options);
	        this.logger = Claypool.Logging.getLogger("Claypool.AOP.AbstractAspect");
        },
        weave: function(){
            var _this = this;
            var pattern;
            var _weave = function(methodName){
                var advice, cutline;//new method , old method
                if(!_this.pointcut){return;}
                _this.hasPrototype = typeof(_this.pointcut.target.prototype) != 'undefined';
        		cutline = _this.hasPrototype ? 
        		    _this.pointcut.target.prototype[methodName] : 
        		    _this.pointcut.target[methodName];
                advice = _this.advise();
                if(!_this.hasPrototype){
        		    _this.pointcut.target[methodName] = advice;
        		}else{ 
        		    _this.pointcut.target.prototype[methodName] = advice;
        		}
        		return { 
        		    advice:advice,
        		    cutline:cutline
        		};
    		};
    		if(this.size===0){//size is empty
                if(this.strategy&&(this.strategy==="matchAll"||this.strategy==="matchFirst")){
                    pattern = new RegExp(this.pointcut.method);
                    for(f in this.pointcut.target){
                        if(jQuery.isFunction(f)&&pattern.test(f)){
                            this.add(Claypool.createGUID(), _weave(f));
                            if(this.strategy==="matchFirst"){break;}
                        }
                    }
                }else{
                    this.add(Claypool.createGUID(), _weave(this.pointcut.method));
                }
            }
            return this;
        },
        unweave: function(){
            var aspect;
            for(var id in this.cache){
                aspect = this.find(id);
               if(!this.hasPrototype){
        		    this.pointcut.target[this.pointcut.method] = aspect.cutline;
    		    } else {
    		        this.pointcut.target.prototype[this.pointcut.method] = aspect.cutline;
    	        }
    			this.pointcut = this.hasPrototype = null;
			}
			this.clear();
			return true;
        },
        advise: function(pointcut, cutline){
            throw new Error("Method not implemented.");
        }
    },
	After$Class:{
	    constructor: function(options){
	        Claypool.extend(true, this, new Claypool.AOP.AbstractAspect(options));
	        Claypool.extend(true, this, Claypool.AOP.After$Class);
	        this.logger = Claypool.Logging.getLogger("Claypool.AOP.After");
	        this.type = "after";
	    },
	    advise: function(){
	        var _this = this;
	        var aspect;
	        return function() {
	            var returnValue;
	            for(id in _this.cache){
	                aspect = _this.find(id);
				    returnValue = aspect.cutline.apply(this, arguments);
				    return _this.pointcut.advice.apply(_this, [returnValue]);
			    }
			};
	    }
	},
	Before$Class: {
	    constructor: function(options){
	        Claypool.extend(true, this, new Claypool.AOP.AbstractAspect(options));
	        Claypool.extend(true, this, Claypool.AOP.Before$Class);
	        this.logger = Claypool.Logging.getLogger("Claypool.AOP.Before");
	        this.type = "before";
	    },
	    advise: function(){
	        var _this = this;
	        return function() {
    			_this.advice.apply(_this, arguments);
    			return _this.cutline.apply(this, arguments);
    		};
	    }
	},
	Around$Class: {
	    constructor: function(options){
	        Claypool.extend(true, this, new Claypool.AOP.AbstractAspect(options));
	        Claypool.extend(true, this, Claypool.AOP.Around$Class);
	        this.logger = Claypool.Logging.getLogger("Claypool.AOP.Around");
	        this.type = "around";
	    },
	    advise: function(){
	        var _this = this;
	        return function() {
    			var invocation = { object: _this, args: arguments };
				return _this.advice.apply(invocation.object, [{ arguments: invocation.args, proceed : 
					function() {
						return _this.cutline.apply(invocation.object, invocation.args);
					}
				}] );
			};
	    }
	},
	/**@exception*/
    ContainerError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.AOP.ContainerError",
                message: "An error occured inside the aop container."
            }));
        }
    },
    /**@exception*/
    AspectFactoryError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.AOP.InstanceFactoryError",
                message: "An error occured in the aop aspect factory."
            }));
        }
    },
    /**@exception*/
    ConfigurationError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.AOP.ConfigurationError",
                message: "An error occured updating the aop container configuration."
            }));
        }
    }
};


/**@constructorAlias*/
Claypool.AOP.Container                      = Claypool.AOP.Container$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.AspectFactory                  = Claypool.AOP.AspectFactory$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.AbstractAspect                 = Claypool.AOP.AbstractAspect$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.After                          = Claypool.AOP.After$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.Before                         = Claypool.AOP.Before$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.Around                         = Claypool.AOP.Around$Class.constructor;

//Exception Classes
/**@constructorAlias*/
Claypool.AOP.ContainerError                 = Claypool.AOP.ContainerError$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.AspectFactoryError             = Claypool.AOP.AspectFactoryError$Class.constructor;
/**@constructorAlias*/
Claypool.AOP.ConfigurationError             = Claypool.AOP.ConfigurationError$Class.constructor;

/**
*   Claypool.IoC
*   @author Christopher Thatcher
*   @version /VERSION/
*/
/**@package*/
Claypool.IoC = {
    /**stores instances and uses an instance factory to
    create new instances if one can't be found (for lazy instantiation patterns)*/
    /**@class*/
    Container$Class:{
        /**@private*/
        instanceFactory:null,
        /**@constructor*/
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.Application.ApplicationContextContributor(options));
            Claypool.extend(true, this, Claypool.IoC.Container$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.IoC.Container");
            this.logger.debug("Configuring Claypool IoC Container");
            /**Register first so any changes to the container managed objects 
            are immediately accessible to the rest of the application aware
            components*/
            this.registerContext("Claypool.IoC.Container");
            this.instanceFactory = new Claypool.IoC.InstanceFactory();
            this.instanceFactory.updateConfigurationCache();
            return this;
        },
        /**@public*/
        get: function(id){
            var instance;
            try{
                this.logger.debug("Search for a container managed instance :%s", id);
                instance = this.find(id);
                if(!instance){
                    this.logger.debug("Can't find a container managed instance :%s", id);
                    instance = this.instanceFactory.getInstance(id);
                    if(instance){
                        this.add(id, instance);
                        return instance._this;
                    }
                }else{
                    this.logger.debug("Found container managed instance :%s", id);
                    return instance._this;
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.InstanceFactoryError(e);
            }
            return null;
        }
    },
    /**Stores instance configurations and manages instance lifecycles*/
    /**@class*/
    InstanceFactory$Class:{
        /**@constructor*/
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.BaseFactory(options));
            Claypool.extend(true, this, Claypool.IoC.InstanceFactory$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.IoC.InstanceFactory");
            return this;
        },
        /**@private*/
        createLifeCycle: function(instance){
            try{
                //Walk the creation lifecycle
                instance.precreate();
                instance.create();
                instance.postcreate();
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.LifeCycleError(e);
            }
        },
        /**@public*/
        getInstance: function(id){
            var configuration;
            var instance;
            try{
                this.logger.debug("Looking for configuration for instance %s", id);
                configuration = this.find(id);
                if(!configuration){
                    this.logger.warn("No known configuration for instance %s", id);
                    return null;
                }else{
                    this.logger.debug("Found configuration for instance %s", id);
                    instance = new Claypool.IoC.Instance(configuration.id, configuration);
                    if(configuration.active&&configuration.selector){
                        this.logger.debug("Attaching contructor to an active selector");
                        _this = this;
                        jQuery(configuration.selector).livequery(function(){
                                _this.createLifeCycle(instance);
                            });
                    }else{
                        this.createLifeCycle(instance);
                    }
                    /**remember this might not be fully initialized yet!*/
                    return instance;
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.InstanceFactoryError(e);
            }
        },
        /**@public*/
        updateConfigurationCache: function(){
            var iocConfiguration;
            var iocconf;
            var i;
            try{
                this.logger.debug("Configuring Claypool IoC InstanceFactory");
                iocConfiguration = this.getConfiguration().ioc||[];
                this.logger.debug("IoC Configurations: %d", iocConfiguration.length);
                for(i=0;i<iocConfiguration.length;i++){
                    try{
                        iocconf = iocConfiguration[i];
                        this.logger.debug("IoC Configuration for Id: %s", iocconf.id);
                        this.add( iocconf.id, iocconf );
                    }catch(e){
                        this.logger.exception(e);
                        return false;
                    }
                }
            }catch(e){
                this.logger.exception(e);
                throw new IoCConfigurationError(e);
            }
            return true;
        }
    },
    /**@class*/
    Instance$Class:{
        _this:null,//A reference to the managed object
        id:null,//published to the application context
        configuration:null,//the instance configuration
        guid:null,//globally (naively) unique id for the instance created internally
        type:null,//a reference to the clazz
        constructor: function(id, configuration){
            Claypool.extend(true, this, Claypool.IoC.Instance$Class);
            /**not a very good guid but naively safe*/
            this.guid =     Math.random();
            this.id   =     id;
            this.configuration = configuration||{};
            this.logger = Claypool.Logging.getLogger("Claypool.IoC.Instance");
            /**Override the category name so we can identify some extra info about the object
            in it's logging statements*/
            this.logger.category = this.logger.category+this.id;
            return this;
        },
        /**
        * 
        */
        /**@public*/
        precreate: function(){
            try{
                this._this = {claypoolId:this.id};//a temporary stand-in for the object we are creating
                this.logger.info("Precreating Instance");
                jQuery(document).trigger("precreate.Claypool.IoC.Instance", [this._this]);
                //TODO:  Apply function specified in ioc hook
                return this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.PreCreatePhaseError(e);
            }
        },
        /**@public*/
        create: function(){
            var selected,factory,factoryClass,factoryMethod,retVal;
            var _this,_thisOrUndefined,C_onstructor,args;
            var injections;
            try{
            /**
            *   This is where we set defaults, but you can also use the lifecycle AOP 
            *   hooks to do something here: like load the defaults from an ajax call
            *   The selector, if it isnt null, is used to create the default value
            *   of '_this' using jQuery.  For now only simple, unique, selectors are allowed
            *   eg anything that is ok to do with $().get(0)
            */
                this.logger.info("Applying Selector and Defaults to Instance");
                if(this.configuration.selector){
                    // binds usto the elements via selector,
                    //and/or sets defaults on the object we are managing (this._this)
                    selected = jQuery(this.configuration.selector)[0];
                    this.logger.debug("Result for selector : ", selected);
                    this._this = jQuery(selected);
                }else{
                    this.logger.debug("Using default empty object");
                    this._this = {};
                }
            /**  
            *   This is where we will create the actual instance from a constructor.
            *   Please use precreate and postcreate to hook you're needs into the
            *   lifecycle process via ioc/aop.
            *   It follows this order:
            *       1. find the appropriate constructor
            *       2. make sure all references in the options are resolved
            *       3. apply the constructor
            */
                if(this.configuration.factory){
                    //The factory is either a managed instance (already constructed)
                    //or it is the name of a factory class to temporarily instantiate
                    factory = {};
                    if(this.configuration.factory.substring(1,0)=="#"){
                        //its a reference to a managed object
                        this.logger.info("Retreiving Factory from Application Context");
                        factory = Claypool.Application.getApplicationContext().get(this.configuration.factory);
                    }else{
                        //Its a class, so instantiate it
                        this.logger.info("Creating Instance from Factory");
                        factoryClass = this.resolveConstructor(this.configuration.factory);
                        retval = factoryClass.apply(factory, this.configuration.options);
                        factory = retval||factory;
                    }
                    this.logger.debug("Applying factory creation method");
                    factoryMethod = this.configuration.factoryMethod||"create";
                    _this = factory[factoryMethod].apply(factory, this.configuration.options);
                    this._this = Claypool.extend(true,  _this, this._this);
                }else{
                    //constructorName is just a simple class constructor
                    /**
                    *   This is here for complex reasons.  There are a plethora ways to instantiate a new object
                    *   with javascript, and to be consistent, regardless of the particular approach, modifying the 
                    *   prototype must do what it supposed to do.. This is the only way I've found to do that.
                    *   PS If your constructor has more than 10 parameters, this wont work.  Why does your constructor
                    *   have more than 10 parameters.
                    */
                    this.logger.info("Creating Instance simulating constructor: %s", this.configuration.clazz);
                    C_onstructor = this.resolveConstructor(this.configuration.clazz);
                    args = this.configuration.options||[];
                    _this = {};
                    switch(args.length){
                        case 0: _this = new C_onstructor();break;
                        case 1: _this = new C_onstructor(args[0]);break;
                        case 2: _this = new C_onstructor(args[0],args[1]);break;
                        case 3: _this = new C_onstructor(args[0],args[1],args[2]);break;
                        case 4: _this = new C_onstructor(args[0],args[1],args[2],args[3]);break;
                        case 5: _this = new C_onstructor(args[0],args[1],args[2],args[3],args[4]);break;
                        case 6: _this = new C_onstructor(args[0],args[1],args[2],args[3],args[4],args[5]);break;
                        case 7: _this = new C_onstructor(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);break;
                        case 8: _this = new C_onstructor(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);break;
                        case 9: _this = new C_onstructor(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);break;
                        default:
                            //this affect the prototype nature so modifying th proto has no affect on the instance
                            _thisOrUndefined = C_onstructor.apply(_this, args);
                            _this = _thisOrUndefined||_this;
                    }
                    this._this = Claypool.extend(true, _this, this._this);
                }
            /**
            *   Now that the object has been successfully created we 'inject' these items
            */
                injections = this.configuration.inject||{};
                Claypool.extend(this._this, injections);
                jQuery(document).trigger("create.Claypool.IoC.Instance", [this._this]);
                return this._this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.CreatePhaseError(e);
            }
        },
        /**@public*/
        postcreate:function(){
            try{
                //TODO:  Apply function specified in ioc hook
                this.logger.info("PostCreate invoked");
                jQuery(document).trigger("postcreate.Claypool.IoC.Instance", [this._this]);
                return this._this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.PostCreatePhaseError(e);
            }
        },
        /**@public*/
        predestroy:function(){
            //If you need to do something to save state, eg make an ajax call to post
            //state to a server or local db (gears), do it here via aop
            try{
                //TODO:  Apply function specified in ioc hook
                this.logger.info("Predestory invoked");
                jQuery(document).trigger("predestroy.Claypool.IoC.Instance", [this._this]);
                return this._this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.PreDestroyPhaseError(e);
            }
        },
        /**@public*/
        destroy:function(){
            try{
                //TODO:  
                //we dont actually do anyting here, yet... it might be
                //a good place to 'delete' or null things
                this.logger.info("Destroy invoked");
                jQuery(document).trigger("destroy.Claypool.IoC.Instance", [this._this]);
                return delete this._this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.DestroyPhaseError(e);
            }
        },
        /**@public*/
        postdestroy:function(){
            //If you need to do something now that the instance was successfully destroyed
            //here is your lifecycle hook.  Connect to it via aop.
            try{
                //TODO:  Apply functions specified in ioc hook
                this.logger.info("Postdestory invoked");
                jQuery(document).trigger("postdestroy.Claypool.IoC.Instance", [this._this]);
                return this;
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.PostDestroyPhaseError(e);
            }
        },
        /**@public*/
        resolveConstructor:function(constructorName){
            var _resolveConstructor;
            var namespaces, constructor;
            var i;
            try{
                _resolveConstructor = function(name){return this[name];};
                
                namespaces = constructorName.split('.');
                constructor = null;
                for( i = 0; i < namespaces.length; i++){
                    constructor = _resolveConstructor.call(constructor,namespaces[i]);
                }
                if( jQuery.isFunction(constructor) ){
                    this.logger.debug(" Resolved " +constructorName+ " to a function");
                    return constructor;
                }else{ 
                    throw new Error("Constructor is not a function: " + constructorName);
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.IoC.ConstructorResolutionError(e);
            }
        }
    },
    /**@exception*/
    ContainerError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.ContainerError",
                message: "An error occured inside the ioc container."
            }));
        }
    },
    /**@exception*/
    InstanceFactoryError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.InstanceFactoryError",
                message: "An error occured in the ioc instance factory."
            }));
        }
    },
    /**@exception*/
    ConfigurationError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.ConfigurationError",
                message: "An error occured updating the ioc container configuration."
            }));
        }
    },
    /**@exception*/
    PreCreatePhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.PreCreatePhaseError",
                message: "An error occured during the 'precreate' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    CreatePhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.CreatePhaseError",
                message: "An error occured during the 'create' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    PostCreatePhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.PostCreatePhaseError",
                message: "An error occured during the 'postcreate' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    PreDestroyPhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.PreDestroyPhaseError",
                message: "An error occured during the 'predestroy' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    DestroyPhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.DestroyPhaseError",
                message: "An error occured during the 'destroy' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    PostDestroyPhaseError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.PostDestroyPhaseError",
                message: "An error occured during the 'postdestroy' lifecycle phase."
            }));
        }
    },
    /**@exception*/
    LifeCycleError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.LifeCycleError",
                message: "An error occured during the lifecycle process."
            }));
        }
    },
    /**@exception*/
    ConstructorResolutionError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.ConstructorResolutionError",
                message: "An error occured trying to resolve the constructor."
            }));
        }
    },
    /**@exception*/
    TypeResolutionError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.IoC.TypeResolutionError",
                message: "An error occured trying to reslove the type."
            }));
        }
    }
};
/**@constructorAlias*/
Claypool.IoC.Container                      = Claypool.IoC.Container$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.InstanceFactory                = Claypool.IoC.InstanceFactory$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.Instance                       = Claypool.IoC.Instance$Class.constructor;

//Exception Classes
/**@constructorAlias*/
Claypool.IoC.ContainerError                 = Claypool.IoC.ContainerError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.InstanceFactoryError           = Claypool.IoC.InstanceFactoryError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.ConfigurationError             = Claypool.IoC.ConfigurationError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.PreCreatePhaseError            = Claypool.IoC.PreCreatePhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.CreatePhaseError               = Claypool.IoC.CreatePhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.PostCreatePhaseError           = Claypool.IoC.PostCreatePhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.PreDestroyPhaseError           = Claypool.IoC.PreDestroyPhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.DestroyPhaseError              = Claypool.IoC.DestroyPhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.PostDestroyPhaseError          = Claypool.IoC.PostDestroyPhaseError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.LifeCycleError                 = Claypool.IoC.LifeCycleError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.ConstructorResolutionError     = Claypool.IoC.ConstructorResolutionError$Class.constructor;
/**@constructorAlias*/
Claypool.IoC.TypeResolutionError            = Claypool.IoC.TypeResolutionError$Class.constructor;/**
* 
*
*/
Claypool.Application={
    applicationContext:null,
    iocContainer:null,
    mvcContainer:null,
    /**@static  @global */
    getApplicationContext: function(){
        if(!Claypool.Application.applicationContext){
            Claypool.Application.applicationContext = new Claypool.Application.ApplicationContext();
        }
        return Claypool.Application.applicationContext;
    },
    /**@static  @global */
    Initialize: function(){
        /**we intentionally do not attempt to try or catch anything here
        If loading the current application-context fails, the app needs to fail*/
        Claypool.Application.iocContainer = new Claypool.IoC.Container();
        //Claypool.Application.aopContainer = new Claypool.AOP.Container();//BUG: AOP is broken
        Claypool.Application.mvcContainer = new Claypool.MVC.Container();
        /**now return the applicationContext ready to use*/
        return Claypool.Application.getApplicationContext();
    },
    /** @static  @global */
    Reinitialize: function(){
        /**we probably should try/catch here*/
        Claypool.Application.iocContainer.instanceFactory.updateConfigurationCache();
        //Claypool.Application.aopContainer.aspectFactory.updateConfigurationCache();//BUG: AOP is broken
        Claypool.Application.mvcContainer.controllerFactory.updateConfigurationCache();
        /**now return the applicationContext ready to use*/
        return Claypool.Application.getApplicationContext();
    },
    /**@class*/
    ApplicationContext$Class:{
        contextContributors:{},
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.AbstractContext(options));
            Claypool.extend(true, this, Claypool.Application.ApplicationContext$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Application.ApplicationContext");
        },
        get: function(id){
            /**we always look in the global first and then through contributors in order*/
            var contextObject;
            var contributor;
            try{
                this.logger.debug("Searching application context for object: %s" ,id);
                contextObject = null;
                contextObject = this.find(id);
                if(contextObject !== null){
                    this.logger.debug("Found object in global application context. Object id: %s", id);
                    return contextObject;
                }else{
                    this.logger.debug("Searching for object in contributed application context. Object id: %s", id);
                    for(contributor in this.contextContributors){
                        contextObject = this.contextContributors[contributor].get(id);
                        if(contextObject !== null){
                            this.logger.debug("Found object in contributed application context. Object id: %s", id);
                            return contextObject;
                        }
                    }
                }
                this.logger.debug("Cannot find object in any application context. Object id: %s", id);
                throw Claypool.Application.NoSuchObjectError(new Error(id));
            }catch(e){
                throw Claypool.Application.ApplicationContextError(e);
            }
        },
        put: function(id, object){
            /**We may want to use a different strategy here so that 'put'
            will look for a matching id and update the entry even in a contributor.*/
            this.logger.debug("Adding object to global application context %s", id);
            this.add(id, object);
        }
    },
    /**@class
    * Extending this class, a container is searched using its 'get' method when
    * anyone looks for something in the applicationContext
    */
    ApplicationContextContributor$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.ContextContributor(options));
            Claypool.extend(true, this, Claypool.Application.ApplicationContextContributor$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Application.ApplicationContextContributor");
            return this;
        },
        registerContext: function(id){
            this.logger.info("Registering Context id: %s", id);
            Claypool.Application.getApplicationContext().contextContributors[id] = this;
        }
    },
    /**@class*/
    ApplicationAware$Class:{
        //The application context is generally provided by the ioc container
        //but other modules can add to it as well.
        constructor: function(options){
            Claypool.extend(true, this, Claypool.Application.ApplicationAware$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Application.ApplicationAware");
        },
        getApplicationContext: function(){
            return Claypool.Application.getApplicationContext();
        }
    },
    /**@exception*/
    NoSuchObjectError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.Application.NoSuchObjectError",
                message: "An error occured trying to locate or load the specified object."
            }));
        }
    },
    /**@exception*/
    ApplicationContextError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.Application.ApplicationContextError",
                message: "An unexpected error occured while searching the application context."
            }));
        }
    }
};
/**@constructorAlias*/
Claypool.Application.ApplicationContext                 = Claypool.Application.ApplicationContext$Class.constructor;
/**@constructorAlias*/
Claypool.Application.ApplicationContextContributor      = Claypool.Application.ApplicationContextContributor$Class.constructor;
/**@constructorAlias*/
Claypool.Application.ApplicationAware                   = Claypool.Application.ApplicationAware$Class.constructor;

//Exception Classes
/**@constructorAlias*/
Claypool.Application.NoSuchObjectError                  = Claypool.Application.NoSuchObjectError$Class.constructor;
/**@constructorAlias*/
Claypool.Application.ApplicationContextError            = Claypool.Application.ApplicationContextError$Class.constructor;
/**
*   Claypool MVC provides some high level built in controllers which a used to 
*   route control to your controllers.  These Claypool provided controllers have a convenient
*   configuration, though in general most controllers, views, and models should be
*   configured using the general ioc configuration patterns. Global Controllers are
*   defined as arrays
*
*   The Claypool built-in controllers are:
*       Claypool.MVC.HijaxLinkController - maps url patterns in hrefs to custom controllers.
*           The href resource is resolved via ajax and the result is delivered to the specified
*           controllers 'handle' method
*
*   eg  "hijax:a" : [
            {
                "id":"#globalLinkController",
                "filter":"",//default value 
                "active":"true",//default value
                "hijaxMap":
                  [{"urls":"http://www.mysite.com/foo*"    ,"controller":"#fooController"},
                   {"urls":"/bar?param=*"                  ,"controller":"#barQueryController"},
                   {"urls":"*"                             ,"controller":"#catchAllController"}]
           }
       ]
*
*       Claypool.MVC.HijaxFormController - maps form submissions to custom controllers.
*           The submittion is handled via ajax and the postback is delivered to the specified
*           controllers 'handle' method
*
*   eg  "hijax:form" : {[
*            {
*               "id":"globalFormController"
*                //general options:
*               "selector":"form.foo",
*               // url patterns
*               "urls":
*                   [{"http://www.mysite.com/foo*"   :"#fooController"},
*                   {"/bar?param=*"                 :"#barQueryController"},
*                   {"*"                            :"#catchAllController"}]
*           }
*        ]}
*
*       Claypool.MVC.EventListenerController - maps events to custom controllers.  This would normally
*           be browser events based on the dom, but with providers like jQuery the eventing
*           is much richer.  By default the event system is provided by jquery.
*
*   eg  "hijax:event":{[
*            {
*               "id":"globalEventController",
*                //general options:
*               "selector":"form.foo",
*               //event_pattern
*               "events":
*                   [{"someEvent*"      :"#fooController"},
*                   {"otherEvent*"     :"#barQueryController"},
*                   {"*"               :"#catchAllEventController"}]
*           }
*        ]}
*
*       Claypool.MVC.AOPInterceptorController - A very cool pattern that 
*
*           A few simple examples can illustrate the useful nature of this built-in controller.
*
        "hijax:aop":{[
            {
               "id":"#globalAOPController",
               "before":
                   [{"ClassName.foo*"           :"#fooController"},
                   {"#containerObject.bar*"     :"#barQueryController"}],
               "around":
                   [{"OtherClassName.foo*"           :"#fooController"},
                   {"#otherContainerObject.bar*"     :"#barQueryController"}],
               "after":
                   [{"AnotherClassName.foo*"           :"#fooController"},
                   {"#anotherContainerObject.bar*"     :"#barQueryController"}],
           }
        ]}
*
*/

Claypool.MVC = {
    Container$Class:{
        controllerFactory:null,
        constructor: function(options){
            $.extend(true, this, new Claypool.Application.ApplicationContextContributor(options));
            $.extend(true, this, Claypool.MVC.Container$Class);
            $.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.Container");
            this.logger.debug("Configuring Claypool MVC Container");
            //Register first so any changes to the container managed objects 
            //are immediately accessible to the rest of the application aware
            //components
            this.registerContext("Claypool.MVC.Container");
            this.controllerFactory = new Claypool.MVC.ControllerFactory();
            this.controllerFactory.updateConfigurationCache();
            //create global contollors non-lazily
            var controller;
            var controllerId;
            for(controllerId in this.controllerFactory.cache){
                //will trigger the controllerFactory to instantiate the controllers
                controller = this.get(controllerId);
                //activates the controller
                this.logger.debug("attaching mvc core controller: %s", controllerId);
                controller.attach();
            }
            return this;
        },
        get: function(id){
            var controller;
            try{
                this.logger.debug("Search for a container managed controller : %s", id);
                controller = this.find(id);
                if(controller===undefined||controller===null){
                    this.logger.debug("Can't find a container managed controller : %s", id);
                    controller = this.controllerFactory.getController(id);
                    if(controller !== null){
                        this.add(id, controller);
                        return controller._this;
                    }
                }else{ 
                    this.logger.debug("Found container managed controller : %s", id);
                    return controller._this;
                }
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.MVC.ContainerError();
            }
            throw new Claypool.MVC.NoSuchControllerError(id);
        }
    },
    ControllerFactory$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.IoC.InstanceFactory(options));
            Claypool.extend(true, this, Claypool.MVC.ControllerFactory$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.ControllerFactory");
        },
        updateConfigurationCache: function(){
            var mvcConfiguration;
            try{
                this.logger.debug("Configuring Claypool MVC ControllerFactory");
                mvcConfiguration = this.getConfiguration().mvc?this.getConfiguration().mvc:{};
                this.initializeHijaxController(mvcConfiguration,"hijax:a",       "Claypool.MVC.HijaxLinkController");
                this.initializeHijaxController(mvcConfiguration,"hijax:form",    "Claypool.MVC.HijaxFormController");
                this.initializeHijaxController(mvcConfiguration,"hijax:event",   "Claypool.MVC.HijaxEventController");
                this.initializeHijaxController(mvcConfiguration,"hijax:server",  "Claypool.Server.HijaxServerController");
                this.initializeHijaxController(mvcConfiguration,"hijax:web",     "Claypool.Server.HijaxWebController");
            }catch(e){
                this.logger.exception(e);
                throw new Claypool.MVC.ConfigurationError(e);
            }
        },
        /**@private*/
        initializeHijaxController: function(mvcConfiguration, key, clazz){
            var configuration;
            var i;
            if(mvcConfiguration[key]){
                for(i=0;i<mvcConfiguration[key].length;i++){
                    configuration = {};
                    configuration.id = mvcConfiguration[key][i].id;
                    configuration.clazz = clazz;
                    configuration.options = [mvcConfiguration[key][i]];
                    this.logger.debug("Adding MVC Configuration for Controller Id: %s", configuration.id);
                    this.add( configuration.id, configuration );
                }
            }
        },   
        /**private*/
        getController: function(id){
            return this.getInstance(id);
        }
    },
    //Basic MVC interfaces
    /**
    *   In Claypool a controller is meant to be a wrapper for a generally 'atomic'
    *   unit of business logic.  
    */
    AbstractController$Class:{ 
        model:null,
        view:null,
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.SimpleCachingStrategy(options));
            Claypool.extend(true, this, Claypool.MVC.AbstractController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.AbstractController");
        },
        handle: function(data){
            throw new Error("Method not implemented");
        },
        resolve: function(mvc){
            throw new Error("Method not implemented");
        },
        attach: function(data){
            throw new Error("Method not implemented");
        },
        detach: function(data){
            throw new Error("Method not implemented");
        }
    },
    /**
    *   The hijax 'or' routing controller implements the handle and resolve methods and provides
    *   a new abstract method 'strategy' which should be a function that return 
    *   a list, possibly empty of controller names to forward the data to.  In general
    *   the strategy can be used to create low level filtering controllers, broadcasting controllers
    *   pattern matching controllers (which may be first match or all matches), etc
    */
    AbstractHijaxController$Class:{
        forwardingList:[],
        filter:"",
        active:true,
        preventDefault:true,
        stopPropagation:true,
        hijaxMap:[],
        strategy:null,
        router:null,
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractController(options));
            Claypool.extend(true, this, Claypool.MVC.AbstractHijaxController$Class);
            Claypool.extend(true, this, options);
            this.router = new Claypool.Router();
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.AbstractHijaxController");
        },
        handle: function(data){
            //Apply the strategy
            this.logger.debug("Handling pattern: %s", data.pattern);
            //this.forwardingList = this.strategy.call(this.router, data.pattern);
            this.forwardingList = this.router[this.strategy||"all"]( data.pattern );
            this.logger.debug("Resolving matched paterns");
            var target, action, resolution;
            var resolvedResponses = [];
            var _this = this;
            return jQuery(this.forwardingList).each(function(){
                try{
                    _this.logger.info("Forwaring to registered controller %s", this.payload.controller);
                    target = Claypool.$(this.payload.controller);
                    resolution = new Claypool.AOP.After({
                        pointcut:{
                            target:target,
                            method:"resolve",
                            advice:function(mvc){
                                resolution.unweave();
                                if(mvc&&mvc.v){
                                    Claypool.$(mvc.v).update(mvc.m);
                                }
                            }
                        }
                    }).weave();
                    target[this.payload.action||"handle"](data.payload);
                    //every controller will return {m:model,v:view,c:controller}
                }catch(e){
                    e = e?e:new Error();
                    if(e.name&&e.name.indexOf("Claypool.Application.NoSuchObjectError")>-1){
                        _this.logger.warn("No controller with id: %s", this.payload.controller);
                    }else{  /**propogate unknown errors*/
                        _this.logger.exception(e); throw e;
                    }
                }
            });
        },
        /**performs required steps to hijax object*/
        hijax: function(target){
            throw new Error("Method not implemented");
        }
    },
    HijaxLinkController$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractHijaxController(options));
            Claypool.extend(true, this, Claypool.MVC.HijaxLinkController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.HijaxLinkController");
            this.strategy = this.strategy||"first";
        },
        attach: function(data){
            this.router.compile(this.hijaxMap, "urls");//, "controller", "action");
            var _this = this;
            this.logger.debug("Is the controller active? %s", this.active);
            if(this.active){
                this.logger.debug("Hijaxing future links.");
                jQuery("a"+this.filter).livequery(function(){
                     _this.hijax(this);
                });
            }else{
                this.logger.debug("Hijaxing present links.");
                jQuery("a"+this.filter).each(function(){
                    _this.hijax(this);
                });
            }
        },
        hijax: function(target){
            this.logger.debug("Hijaxing link: %s", target);
            var _this = this;
            jQuery(target).bind("click.Claypool.MVC.HijaxLinkController", function(event){
                _this.logger.info("Hijaxing link: %s ", event.target);
                if(_this.preventDefault){
                    _this.logger.debug("Preventing default link behaviour");
                    event.preventDefault();
                }
                if(_this.stopPropagation){
                    _this.logger.debug("Stopping propogation of link");
                    event.stopPropagation();
                }
                _this.handle({pattern:jQuery(this).attr("href"), payload:event});
            }); 
        }
    },
    HijaxFormController$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractHijaxController(options));
            Claypool.extend(true, this, Claypool.MVC.HijaxFormController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.HijaxFormController");
            this.strategy = this.strategy||"first";
        },
        attach: function(data){
            this.router.compile(this.hijaxMap, "urls");//, "controller", "action");
            var _this = this;
            if(this.active){
                jQuery("form"+this.filter).livequery(function(){
                    _this.hijax(this);
                });
            }else{
                jQuery("form"+this.filter).each(function(){
                    _this.hijax(this);
                });
            }
        },
        hijax: function(target){
            var _this = this;
            jQuery(target).bind("submit.Claypool.MVC.HijaxFormController", function(event){
                var retVal = true;
                _this.logger.info("Hijaxing form: ", event);
                if(_this.stopPropagation){
                    _this.logger.debug("Stopping propogation of form");
                    event.stopPropagation();
                }
                if(_this.preventDefault){
                    _this.logger.debug("Preventing default form behaviour");
                    event.preventDefault();
                    retVal = false;
                }
                _this.handle({pattern:this.action, payload:event});
                return retVal;
            });
        }
    },
    HijaxEventController$Class:{
        eventNamespace:".Claypool.MVC.HijaxEventController",
        bindCache:null,
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractHijaxController(options));
            Claypool.extend(true, this, Claypool.MVC.HijaxEventController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.MVC.HijaxEventController");
            this.bindCache = new Claypool.SimpleCachingStrategy();
            this.strategy = this.strategy||"all";
        },
        attach: function(data){
            this.router.compile(this.hijaxMap, "event");//, "controller", "action");
            var _this = this;
            if(this.active&&this.filter!==""){
                jQuery(this.filter).livequery(function(){
                    _this.hijax(this);
                });
            }else if (this.filter!==""){
                jQuery(this.filter).each(function(){
                    _this.hijax(this);
                });
            }else if(document!==undefined){
                _this.hijax(document);
            }else{this.logger.warn("Unable to attach controller: %s", options.id);}
        },
        hijax: function(target){
            var _this = this;
            jQuery(this.hijaxMap).each(function(){
                /**Only bind to the event once as we will progagate the event
                to each matching registerer, but dont want this low level handler
                invoked more than once!*/
                if(!_this.bindCache.find(this.event)){
                    _this.bindCache.add(this.event, _this);
                    _this.logger.debug("Binding event %s to controller %s  on target %s",
                        this.event, this.controller ,target);
                    jQuery(target).bind(this.event+_this.eventNamespace,
                        function(event){
                            _this.logger.info("Hijaxing event : ", event);
                            if(_this.stopPropagation){//BUG: should use ==="true" but this is skipped
                                _this.logger.debug("Stopping propogation of event");
                                event.stopPropagation();
                            }
                            if(_this.preventDefault){
                                _this.logger.debug("Preventing default event behaviour");
                                event.preventDefault();
                            }
                           _this.handle({pattern:event.type, payload:event});
                    });
                }
            });
            return true;
        }
    },
    AbstractView$Class:{
        constructor: function(options){
            Claypool.extend(true, this, Claypool.MVC.AbstractView$Class);
            Claypool.extend(true, this, options);
        },
        update: function(model){//refresh screen display logic
            throw new Error("Method not implemented.");
        },
        think: function(){//display activity occuring, maybe block
            throw new Error("Method not implemented.");
        }
    },
    /**@exception*/
    ContainerError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.MVC.ContainerError",
                message: "An error occurred trying to retreive a container managed object."
            }));
        }
    },
    /**@exception*/
    NoSuchControllerError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.MVC.NoSuchControllerError",
                message: "Cant find the controller"
            }));
        }
    },
    /**@exception*/
    AOPResolutionError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.MVC.AOPResolutionError",
                message: "An error occured resolving the aop expression."
            }));
        }
    },
    /**@exception*/
    ConfigurationError$Class:{
        constructor: function(e){
            Claypool.extend(true, this, new Claypool.Error(e, {
                name:"Claypool.MVC.ConfigurationError",
                message: "An error occured during the configuration."
            }));
        }
    }
};
/**@constructorAlias*/
Claypool.MVC.Container                      = Claypool.MVC.Container$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.ControllerFactory              = Claypool.MVC.ControllerFactory$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.AbstractController             = Claypool.MVC.AbstractController$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.AbstractHijaxController        = Claypool.MVC.AbstractHijaxController$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.HijaxLinkController            = Claypool.MVC.HijaxLinkController$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.HijaxFormController            = Claypool.MVC.HijaxFormController$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.HijaxEventController           = Claypool.MVC.HijaxEventController$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.AbstractView                   = Claypool.MVC.AbstractView$Class.constructor;


//Exception Classes
/**@constructorAlias*/
Claypool.MVC.ContainerError                 = Claypool.MVC.ContainerError$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.NoSuchControllerError          = Claypool.MVC.NoSuchControllerError$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.ConfigurationError             = Claypool.MVC.ConfigurationError$Class.constructor;
/**@constructorAlias*/
Claypool.MVC.AOPResolutionError             = Claypool.MVC.AOPResolutionError$Class.constructor;Claypool.Server = {
    serverServletContainer:null,
    isServletContainerInitialized:false,
    webProxyContainer:null,
    isProxyContainerInitialized:false,
    logger: null,
    handle: function(request, response){
        var applicationContext;
        Claypool.Server.logger = Claypool.Server.logger?Claypool.Server.logger:
            Claypool.Logging.getLogger("Claypool.Server");
        Claypool.Server.logger.debug("Starting Serving Request...");
        if(!Claypool.Server.serverServletContainer&&!Claypool.Server.isServletContainerInitialized){
            Claypool.Server.logger.info("Initializing global servlet controller");
            try{
                Claypool.Server.serverServletContainer = Claypool.$("#serverServletContainer");
            }catch(e){Claypool.Server.logger.exception(e);}
            Claypool.Server.isServletContainerInitialized = true;
        }
        if(!Claypool.Server.webProxyContainer&&!Claypool.Server.isProxyContainerInitialized){
            Claypool.Server.logger.info("Initializing global proxy controller");
            try{
                Claypool.Server.webProxyContainer = Claypool.$("#webProxyContainer");
            }catch(e){Claypool.Server.logger.exception(e);}
            Claypool.Server.isProxyContainerInitialized = true;
        }
        Claypool.Server.logger.info("Handling global request routing for request: %s", request.requestURL);
        if(request.pathInfo.match("/webproxy")){
                Claypool.Server.logger.debug("Dispatching request to Web Proxy Container");
                response = Claypool.Server.webProxyContainer.hijax({
                    request:request, 
                    response: response
                });
         }else{
                Claypool.Server.logger.debug("Dispatching request to Server Sevlet Container");
                response = Claypool.Server.serverServletContainer.hijax({
                    request:request, 
                    response: response
                });
        }
        Claypool.Server.logger.debug("Finished Serving Request...");
        Claypool.Server.logger.debug("Response Code: %s", response.headers.status);
        return response;
    },
    HijaxServerController$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractHijaxController(options));
            Claypool.extend(true, this, Claypool.Server.HijaxServerController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Server.HijaxServerController");
            this.router = new Claypool.MVC.RegExpRouter();
            this.strategy = this.router.matchFirst;
        }, 
        hijax: function(target){
            this.logger.debug("Hijaxing servlet target");
            //synchronis
            var targetPath = target.request.pathInfo     ?target.request.pathInfo    :"/" +
                             target.request.queryString  ?target.request.queryString :"";
            var servletResponseList = this.handle({
                pattern:targetPath,
                payload:target
            });
            var response;
            if(servletResponseList.length === 0){
                this.logger.debug("No match found for server target path : %s", target.request.pathInfo);
                response = target.response?target.response:{};
                response.headers = response.headers?response.headers:{};
                response.headers.contentType = 'text/plain';
                response.headers.status = 404;
                response.body = "NO MATCH FOUND";
                servletResponseList.push(response);
            }
            return servletResponseList[0];
            
        },
        attach: function(data){
            this.logger.debug("Attaching Controller");
            this.router.compile(this.hijaxMap, "urls", "controller");
            this.logger.debug("Attached Controller");
        }
    },
    HijaxWebController$Class:{
        webProxyServlet:null,
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractHijaxController(options));
            Claypool.extend(true, this, Claypool.Server.HijaxWebController$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Server.HijaxWebController");
            this.router = new Claypool.MVC.RegExpRouter();
            this.strategy = this.router.matchFirst;
            this.webProxyServlet = new Claypool.Server.WebProxyServlet(options);
        }, 
        hijax: function(target){
            this.logger.debug("Hijaxing web via proxy");
            return this.webProxyServlet.handle(target);
        },
        attach: function(data){
            this.logger.debug("Attaching Controller");
            this.router.compile(this.hijaxMap, "urls", "#webProxyContainter");
            this.logger.debug("Attached Controller");
        }
    },
    AbstractServlet$Class:{
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.MVC.AbstractController(options));
            Claypool.extend(true, this, Claypool.Server.AbstractServlet$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Server.AbstractServlet");
        },
        //expect data.request and data.response
        //We reduce to a single response handler function because it's not easy to
        //support the asynch stuff on the server side
        handle: function(data){
            var request = data&&data.request?data.request:{method:"GET"};
            var response = data&&data.response?data.response:{};
            Claypool.extend(true, request, {
                method:'GET'
            });
            Claypool.extend(true, response.headers, {
                contentType:'text/plain',
                status: 404
            });
            try{
                switch(request.method.toUpperCase()){
                    case 'GET':
                        this.logger.debug("Handling GET request");
                        this.handleGet(request, response);
                        break;
                    case 'POST':
                        this.logger.debug("Handling POST request");
                        this.handlePost(request, response);
                        break;
                    case 'PUT':
                        this.logger.debug("Handling PUT request");
                        this.handlePut(request, response);
                        break;
                    case 'DELETE':
                        this.logger.debug("Handling DELETE request");
                        this.handleDelete(request, response);
                        break;
                    case 'HEAD':
                        this.logger.debug("Handling HEAD request");
                        this.handleHead(request, response);
                        break;
                    case 'OPTIONS':
                        this.logger.debug("Handling OPTIONS request");
                        this.handleOptions(request, response);
                        break;
                    default:
                        this.logger.debug("Unknown Method: %s, rendering error response.",  request.method);
                        this.handleError(request, response, "Unknown Method: " + request.method );
                }
            } catch(e) {
                this.logger.exception(e);
                this.handleError(request, response, "Caught Exception in Servlet handler", e);
            }finally{
                return this.resolve({request:request, response:response});
            }
        },
        handleGet: function(request, response){
            throw new Error("Method not implemented");
        },
        handlePost: function(request, response){
            throw new Error("Method not implemented");
        },
        handlePut: function(request, response){
            throw new Error("Method not implemented");
        },
        handleDelete: function(request, response){
            throw new Error("Method not implemented");
        },
        handleHead: function(request, response){
            throw new Error("Method not implemented");
        },
        handleOptions: function(request, response){
            throw new Error("Method not implemented");
        },
        handleError: function(request, response, msg, e){
            this.logger.warn("The default error response should be overriden");
            response.headers.status = 300;
            response.body = msg?msg:"Unknown internal error\n";
            response.body += e&&e.msg?e.msg:"\n";
        },
        resolve: function(data){
            this.logger.warn("The default resolve response is meant to be overriden to allow the rendering of a custom view.");
            return data.response;
        }
    },
    WebProxyServlet$Class : {
        constructor: function(options){
            Claypool.extend(true, this, new Claypool.Server.AbstractServlet(options));
            Claypool.extend(true, this, Claypool.Server.WebProxyServlet$Class);
            Claypool.extend(true, this, options);
            this.logger = Claypool.Logging.getLogger("Claypool.Server.WebProxyServlet");
        },
        handleGet: function(request, response){
            var _this = this;
            var proxyUrl = request.parameters.proxyUrl;
            //dont propogate this parameter on the proxy
            delete request.parameters.proxyUrl;
            _this.logger.debug("Proxying get request to: %s", proxyUrl);
            jQuery.ajax({
                type:"GET",
                dataType:"text",
                async:false,
                data:request.parameters,
                url:proxyUrl,
                beforeSend: function(xhr){
                    for(var header in request.headers){
                        xhr.setRequestHeader(header, request.headers[header]);
                    }
                    response.headers = {};
                },
                success: function(xml){
                    _this.logger.debug("Got response for proxy.");
                    response.body = xml.toString();
                    response.headers.status = 200;
                },
                error: function(xml, status, e){
                    _this.logger.error("Error proxying request. STATUS: %s", status?status:"UNKNOWN");
                    if(e){_this.logger.exception(e);}
                    response.headers.status = 500;
                },
                complete: function(xhr, status){
                    _this.logger.debug("Proxy Request Complete, Copying response headers");
                    var proxyResponseHeaders = xhr.getAllResponseHeaders();
                    var responseHeader;
                    var responseHeaderMap;
                    _this.logger.debug("Complete Proxy response header: %s" ,proxyResponseHeaders);
                    proxyResponseHeaders = proxyResponseHeaders.split("\r\n");
                    for(var i = 0; i < proxyResponseHeaders.length; i++){
                        responseHeaderMap = proxyResponseHeaders[i].split(":");
                        try{
                            _this.logger.debug("setting response header %s", responseHeaderMap[0]);
                            response.headers[responseHeaderMap.shift()] = responseHeaderMap.join(":");
                        }catch(e){
                            _this.logger.warn("Unable to set a proxied response header");
                            _this.logger.exception(e);
                        }
                    }
                    response.headers["Claypool-Proxy"] = proxyUrl;
                }
            });
            return response;
        }
    }
};
/**@constructorAlias*/
ClaypoolServerHandler = Claypool.Server.handle;

//Some server side classes that are otherwise not used on the client
/**@constructorAlias*/
Claypool.Server.HijaxServerController           = Claypool.Server.HijaxServerController$Class.constructor;
/**@constructorAlias*/
Claypool.Server.HijaxWebController              = Claypool.Server.HijaxWebController$Class.constructor;
/**@constructorAlias*/
Claypool.Server.AbstractServlet                 = Claypool.Server.AbstractServlet$Class.constructor;
/**@constructorAlias*/
Claypool.Server.WebProxyServlet                 = Claypool.Server.WebProxyServlet$Class.constructor;})();