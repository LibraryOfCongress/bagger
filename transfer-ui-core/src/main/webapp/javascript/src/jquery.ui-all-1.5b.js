(function($) {
	
	//If the UI scope is not available, add it
	$.ui = $.ui || {};
	
	//Add methods that are vital for all mouse interaction stuff (plugin registering)
	$.extend($.ui, {
		plugin: {
			add: function(module, option, set) {
				var proto = $.ui[module].prototype;
				for(var i in set) {
					proto.plugins[i] = proto.plugins[i] || [];
					proto.plugins[i].push([option, set[i]]);
				}
			},
			call: function(instance, name, arguments) {
				var set = instance.plugins[name]; if(!set) return;
				for (var i = 0; i < set.length; i++) {
					if (instance.options[set[i][0]]) set[i][1].apply(instance.element, arguments);
				}
			}	
		},
		cssCache: {},
		css: function(name) {
			if ($.ui.cssCache[name]) return $.ui.cssCache[name];
			
			var tmp = $('<div class="ui-resizable-gen">').addClass(name).css(
				{position:'absolute', top:'-5000px', left:'-5000px', display:'block'}
			).appendTo('body');
			
			//Opera and Safari set width and height to 0px instead of auto
			//Safari returns rgba(0,0,0,0) when bgcolor is not set
			$.ui.cssCache[name] = !!(
				((/^[1-9]/).test(tmp.css('height')) || (/^[1-9]/).test(tmp.css('width')) || 
				!(/none/).test(tmp.css('backgroundImage')) || !(/transparent|rgba\(0, 0, 0, 0\)/).test(tmp.css('backgroundColor')))
			);
			try { $('body').get(0).removeChild(tmp.get(0));	} catch(e){}
			return $.ui.cssCache[name];
		},
		disableSelection: function(e) {
			if (!e) return;
			e.unselectable = "on";
			e.onselectstart = function() {	return false; };
			if (e.style) e.style.MozUserSelect = "none";
		},
		enableSelection: function(e) {
			if (!e) return;
			e.unselectable = "off";
			e.onselectstart = function() { return true; };
			if (e.style) e.style.MozUserSelect = "";
		}
	});
	
	/********************************************************************************************************/

	$.fn.extend({
		mouseInteraction: function(o) {
			return this.each(function() {
				new $.ui.mouseInteraction(this, o);
			});
		},
		removeMouseInteraction: function(o) {
			return this.each(function() {
				if($.data(this, "ui-mouse"))
					$.data(this, "ui-mouse").destroy();
			});
		}
	});
	
	/********************************************************************************************************/
	
	$.ui.mouseInteraction = function(element, options) {
	
		var self = this;
		this.element = element;
		$.data(this.element, "ui-mouse", this);
		this.options = $.extend({}, options);
		
		$(element).bind('mousedown.draggable', function() { return self.click.apply(self, arguments); });
		if($.browser.msie) $(element).attr('unselectable', 'on'); //Prevent text selection in IE
		
	};
	
	$.extend($.ui.mouseInteraction.prototype, {
		
		destroy: function() { $(this.element).unbind('mousedown.draggable'); },
		trigger: function() { return this.click.apply(this, arguments); },
		click: function(e) {
			
			if(
				   e.which != 1 //only left click starts dragging
				|| $.inArray(e.target.nodeName.toLowerCase(), this.options.dragPrevention) != -1 // Prevent execution on defined elements
				|| (this.options.condition && !this.options.condition.apply(this.options.executor || this, [e, this.element])) //Prevent execution on condition
			) return true;
			
			var self = this;
			var initialize = function() {
				self._MP = { left: e.pageX, top: e.pageY }; // Store the click mouse position
				$(document).bind('mouseup.draggable', function() { return self.stop.apply(self, arguments); });
				$(document).bind('mousemove.draggable', function() { return self.drag.apply(self, arguments); });
			};

			if(this.options.delay) {
				if(this.timer) clearInterval(this.timer);
				this.timer = setTimeout(initialize, this.options.delay);
			} else {
				initialize();
			}
			
			return false;
			
		},
		stop: function(e) {			
			
			var o = this.options;
			if(!this.initialized) return $(document).unbind('mouseup.draggable').unbind('mousemove.draggable');

			if(this.options.stop) this.options.stop.call(this.options.executor || this, e, this.element);
			$(document).unbind('mouseup.draggable').unbind('mousemove.draggable');
			this.initialized = false;
			return false;
			
		},
		drag: function(e) {

			var o = this.options;
			if ($.browser.msie && !e.button) return this.stop.apply(this, [e]); // IE mouseup check
			
			if(!this.initialized && (Math.abs(this._MP.left-e.pageX) >= o.distance || Math.abs(this._MP.top-e.pageY) >= o.distance)) {
				if(this.options.start) this.options.start.call(this.options.executor || this, e, this.element);
				this.initialized = true;
			} else {
				if(!this.initialized) return false;
			}

			if(o.drag) o.drag.call(this.options.executor || this, e, this.element);
			return false;
			
		}
	});

 })(jQuery);(function($) {
  
  $.fn.resizable = function(options) {
    return this.each(function() {
      var args = Array.prototype.slice.call(arguments, 1);
      
      if (typeof options == "string") {
        var resize = $.data(this, "ui-resizable");
        resize[options].apply(resize, args);

      } else if(!$(this).is(".ui-resizable"))
        new $.ui.resizable(this, options);
        
    });
  };
  
  $.ui.resizable = function(element, options) {
    //Initialize needed constants
    var self = this;
    
    this.element = $(element);
    
    $.data(element, "ui-resizable", this);
    this.element.addClass("ui-resizable");
    
    //Prepare the passed options
    this.options = $.extend({
      preventDefault: true,
      transparent: false,
      minWidth: 10,
      minHeight: 10,
      aspectRatio: false,
      disableSelection: true,
      preserveCursor: true,
      autohide: false
    }, options);
    
		this.options._aspectRatio = !!(this.options.aspectRatio);
		
    $(element).bind("setData.resizable", function(event, key, value){
      self.options[key] = value;
    }).bind("getData.resizable", function(event, key){
      return self.options[key];
    });
    
    var o = this.options;
    
    //Default Theme
    var aBorder = '1px solid #DEDEDE';
    
    o.defaultTheme = {
      'ui-resizable': { display: 'block' },
      'ui-resizable-handle': { position: 'absolute', background: '#F5F5F5' },
      'ui-resizable-n': { cursor: 'n-resize', height: '4px', left: '0px', right: '0px', borderTop: aBorder },
      'ui-resizable-s': { cursor: 's-resize', height: '4px', left: '0px', right: '0px', borderBottom: aBorder },
      'ui-resizable-e': { cursor: 'e-resize', width: '4px', top: '0px', bottom: '0px', borderRight: aBorder },
      'ui-resizable-w': { cursor: 'w-resize', width: '4px', top: '0px', bottom: '0px', borderLeft: aBorder },
      'ui-resizable-se': { cursor: 'se-resize', width: '4px', height: '4px', borderRight: aBorder, borderBottom: aBorder },
      'ui-resizable-sw': { cursor: 'sw-resize', width: '4px', height: '4px', borderBottom: aBorder, borderLeft: aBorder },
      'ui-resizable-ne': { cursor: 'ne-resize', width: '4px', height: '4px', borderRight: aBorder, borderTop: aBorder },
      'ui-resizable-nw': { cursor: 'nw-resize', width: '4px', height: '4px', borderLeft: aBorder, borderTop: aBorder }
    };
    
    //Position the node
    if(!o.proxy && (this.element.css('position') == 'static' || this.element.css('position') == ''))
      this.element.css('position', 'relative');
    
    var nodeName = element.nodeName;
    
    //Wrap the element if it cannot hold child nodes
    if(nodeName.match(/textarea|input|select|button|img/i)) {
      
      //Create a wrapper element and set the wrapper to the new current internal element
      this.element.wrap('<div class="ui-wrapper"  style="overflow: hidden; position: relative; width: '+this.element.outerWidth()+'px; height: '+this.element.outerHeight()+';"></div>');
      var oel = this.element; element = element.parentNode; this.element = $(element);
      
      //Move margins to the wrapper
      this.element.css({ marginLeft: oel.css("marginLeft"), marginTop: oel.css("marginTop"),
        marginRight: oel.css("marginRight"), marginBottom: oel.css("marginBottom")
      });
      
      oel.css({ marginLeft: 0, marginTop: 0, marginRight: 0, marginBottom: 0});
      
      //Prevent Safari textarea resize
      if ($.browser.safari && o.preventDefault) oel.css('resize', 'none');
      
      o.proportionallyResize = oel.css('position', 'static');
			
			// fix handlers offset
			this._proportionallyResize();
    }
    
    if(!o.handles) o.handles = !$('.ui-resizable-handle', element).length ? "e,s,se" : { n: '.ui-resizable-n', e: '.ui-resizable-e', s: '.ui-resizable-s', w: '.ui-resizable-w', se: '.ui-resizable-se', sw: '.ui-resizable-sw', ne: '.ui-resizable-ne', nw: '.ui-resizable-nw' };
    if(o.handles.constructor == String) {

      if(o.handles == 'all')
        o.handles = 'n,e,s,w,se,sw,ne,nw';
        
      var n = o.handles.split(","); o.handles = {};
      
      o.zIndex = o.zIndex || 1000;
      
      var insertions = {
				handle: 'overflow:hidden; position:absolute;',
        n: 'top: 0pt; width:100%;',
        e: 'right: 0pt; height:100%;',
        s: 'bottom: 0pt; width:100%;',
        w: 'left: 0pt; height:100%;',
        se: 'bottom: 0pt; right: 0px;',
        sw: 'bottom: 0pt; left: 0px;',
        ne: 'top: 0pt; right: 0px;',
        nw: 'top: 0pt; left: 0px;'
      };
      
      for(var i = 0; i < n.length; i++) {
        var d = jQuery.trim(n[i]), t = o.defaultTheme, hname = 'ui-resizable-'+d;
        
        var rcss = $.extend(t[hname], t['ui-resizable-handle']), 
            axis = $(["<div class=\"",hname," ui-resizable-handle\" style=\"",insertions[d], insertions.handle,"\"></div>"].join("")).css(/sw|se|ne|nw/.test(d) ? { zIndex: ++o.zIndex } : {});
        
        o.handles[d] = '.ui-resizable-'+d;
          
        this.element.append(
          //Theme detection, if not loaded, load o.defaultTheme
          axis.css( !$.ui.css(hname) ? rcss : {} )
        );
      }
    }
    
    this._renderAxis = function(target) {
      target = target || this.element;
      
      for(var i in o.handles) {
        if(o.handles[i].constructor == String) 
          o.handles[i] = $(o.handles[i], element).show();
        
        if (o.transparent)
          o.handles[i].css({opacity:0});
        
        //Apply pad to wrapper element, needed to fix axis position (textarea, inputs, scrolls)
        if (this.element.is('.ui-wrapper') && 
          nodeName.match(/textarea|input|select|button/i)) {
            
          var axis = $(o.handles[i], element), padWrapper = 0;
          
          //Checking the correct pad and border
          padWrapper = /sw|ne|nw|se|n|s/.test(i) ? axis.outerHeight() : axis.outerWidth();
          
          //The padding type i have to apply...
          var padPos = [ 'padding', 
            /ne|nw|n/.test(i) ? 'Top' :
            /se|sw|s/.test(i) ? 'Bottom' : 
            /^e$/.test(i) ? 'Right' : 'Left' ].join(""); 
          
          if (!o.transparent)
            target.css(padPos, padWrapper);
        }
        if(!$(o.handles[i]).length) continue;
      }
    };
    
    this._renderAxis(this.element);
    var handlers = $('.ui-resizable-handle', self.element);
    
    if (o.disableSelection)
      handlers.each(function(i, e) { $.ui.disableSelection(e); });
    
    //Matching axis name
    handlers.mouseover(function() {
      if (!o.resizing) {
        if (this.className) 
          var axis = this.className.match(/ui-resizable-(se|sw|ne|nw|n|e|s|w)/i);
        //Axis, default = se
        o.axis = axis && axis[1] ? axis[1] : 'se';
      }
    });
        
    //If we want to auto hide the elements
    if (o.autohide) {
      var tLoaded = $.ui.css('ui-resizable-s') || $.ui.css('ui-resizable-e');
      if (!tLoaded) handlers.hide();
      
      $(self.element).addClass("ui-resizable-autohide").hover(function(){
        if (!tLoaded) handlers.show();
        $(this).removeClass("ui-resizable-autohide");
      }, function(){
        if (!o.resizing) {
          if (!tLoaded) handlers.hide();
          $(this).addClass("ui-resizable-autohide");
        }
      });
    }
  
    //Initialize mouse events for interaction
    this.element.mouseInteraction({
      executor: this,
      delay: 0,
      distance: 0,
      dragPrevention: ['input','textarea','button','select','option'],
      start: this.start,
      stop: this.stop,
      drag: this.drag,
      condition: function(e) {
        if(this.disabled) return false;
        for(var i in this.options.handles) {
          if($(this.options.handles[i])[0] == e.target) return true;
        }
        return false;
      }
    });
  };
  
  $.extend($.ui.resizable.prototype, {
    plugins: {},
    ui: function() {
      return {
        instance: this,
        axis: this.options.axis,
        options: this.options
      };      
    },
    _proportionallyResize: function() {
			var o = this.options;

			if (!o.proportionallyResize)
				return;
			
			var prel = o.proportionallyResize;
			
      var b = [ prel.css('borderTopWidth'), prel.css('borderRightWidth'), prel.css('borderBottomWidth'), prel.css('borderLeftWidth') ];
      var p = [ prel.css('paddingTop'), prel.css('paddingRight'), prel.css('paddingBottom'), prel.css('paddingLeft') ];
      
      o.borderDif = o.borderDif || $.map(b, function(v, i) {
        var border = parseInt(v,10)||0, padding = parseInt(p[i],10)||0;
        return border + padding; 
      });
			
      prel.css({
        display: 'block', //Needed to fix height autoincrement
        height: (this.element.height() - o.borderDif[0] - o.borderDif[2]) + "px",
        width: (this.element.width() - o.borderDif[1] - o.borderDif[3]) + "px"
      });
    },
    _renderProxy: function() {
      var el = this.element, o = this.options;
      this.offset = el.offset();
      
      if(o.proxy) {
        this.helper = this.helper || $('<div style="overflow:hidden;"></div>');
				
				// fix ie6 offset
				var ie6offset = ($.browser.msie && $.browser.version < 7 ? 3 : 0);
				
        this.helper.addClass(o.proxy).css({
          width: el.outerWidth(),
          height: el.outerHeight(),
          position: 'absolute',
          left: this.offset.left - ie6offset +'px',
          top: this.offset.top - ie6offset +'px',
          zIndex: ++o.zIndex
        });
        
        this.helper.appendTo("body");
        
        if (o.disableSelection)
          $.ui.disableSelection(this.helper.get(0));
            
      } else {
        this.helper = el; 
      }
    },
    propagate: function(n,e) {
      $.ui.plugin.call(this, n, [e, this.ui()]);
      this.element.triggerHandler(n == "resize" ? n : "resize"+n, [e, this.ui()], this.options[n]);
    },
    destroy: function() {
      this.element
        .removeClass("ui-resizable ui-resizable-disabled")
        .removeMouseInteraction()
        .removeData("ui-resizable")
        .unbind(".resizable");
    },
    enable: function() {
      this.element.removeClass("ui-resizable-disabled");
      this.disabled = false;
    },
    disable: function() {
      this.element.addClass("ui-resizable-disabled");
      this.disabled = true;
    },
    start: function(e) {
      var o = this.options, iniPos = this.element.position(), el = this.element;
      o.resizing = true;
			o.documentScroll = { top: $(document).scrollTop(),	left: $(document).scrollLeft() };
			
			// buf fix #1749
      if (el.is('.ui-draggable') || (/absolute/).test(el.css('position'))) {
				// sOffset decides if document scrollOffset will be added to the top/left of the resizable element
				var sOffset = $.browser.msie && !o.containment && (/absolute/).test(el.css('position')) && !(/relative/).test(el.parent().css('position'));
				var dscrollt = sOffset ? o.documentScroll.top : 0, dscrolll = sOffset ? o.documentScroll.left : 0;
	  		el.css({ position: 'absolute', top: (iniPos.top + dscrollt),	left: (iniPos.left + dscrolll)	});
	  	}
      
      //Opera fixing relative position
      if (/relative/.test(el.css('position')) && $.browser.opera)
        el.css({ position: 'relative', top: 'auto', left: 'auto' });
      
      this._renderProxy();
      
			var curleft = parseInt(this.helper.css('left'),10) || 0, curtop = parseInt(this.helper.css('top'),10) || 0;
			
      //Store needed variables
      $.extend(o, {
        currentSize: { width: el.outerWidth(), height: el.outerHeight() },
        currentSizeDiff: { width: el.outerWidth() - el.width(), height: el.outerHeight() - el.height() },
        startMousePosition: { left: e.pageX, top: e.pageY },
        startPosition: { left: curleft, top: curtop },
        currentPosition: { left: curleft,top: curtop }
      });

			//Aspect Ratio
			var iswlt = o.currentSize.width < o.currentSize.height;
 			o.aspectRatio = (typeof o.aspectRatio == 'number') ? o.aspectRatio : Math.pow(o.currentSize.width / o.currentSize.height, iswlt ? 1 : -1);
			o.aspectRatioTarget = iswlt ? "width" : "height";
			
      if (o.preserveCursor)
        $('body').css('cursor', o.axis + '-resize');
      
      if (o.containment) {
        var oc = o.containment,
           ce = (oc instanceof jQuery) ? oc.get(0) : 
              (/parent/.test(oc)) ? el.parent().get(0) : null;
        if (ce) {
          
          var scroll = function(e, a) {
            var scroll = /top/.test(a||"top") ? 'scrollTop' : 'scrollLeft', has = false;
            if (e[scroll] > 0) return true; e[scroll] = 1;
            has = e[scroll] > 0 ? true : false; e[scroll] = 0;
            return has; 
          };
          
          var co = $(ce).offset(), ch = $(ce).innerHeight(), cw = $(ce).innerWidth();
          o.cdata = { e: ce, l: co.left, t: co.top, w: (scroll(ce, "left") ? ce.scrollWidth : cw ), h: (scroll(ce) ? ce.scrollHeight : ch) };
        }
        if (/document/.test(oc) || oc == document) o.cdata = { e: document, l: 0, t: 0, w: $(document).width(), h: $(document).height() };
      }
      this.propagate("start", e);   
      return false;
      
    },
    stop: function(e) {
      this.options.resizing = false;
      var o = this.options;
      
      if(o.proxy) {
        var style = { 
          width: (this.helper.width() - o.currentSizeDiff.width) + "px",
          height: (this.helper.height() - o.currentSizeDiff.height) + "px",
          top: ((parseInt(this.element.css('top'),10) || 0) + ((parseInt(this.helper.css('top'),10) - this.offset.top)||0)),
          left: ((parseInt(this.element.css('left'),10) || 0) + ((parseInt(this.helper.css('left'),10) - this.offset.left)||0))
        };
       	this.element.css(style);
        if (o.proxy) this._proportionallyResize();
        this.helper.remove();
      }
      
      if (o.preserveCursor)
        $('body').css('cursor', 'auto');
      
      this.propagate("stop", e);  
      return false;
      
    },
    drag: function(e) {
      //Increase performance, avoid regex
      var el = this.helper, o = this.options, props = {}, 
						self = this, pRatio = o._aspectRatio || e.shiftKey;
			
      var change = function(a,b) {
        var isth = (a=="top"||a=="height"), ishw = (a=="width"||a=="height"),
							defAxis = (o.axis=="se"||o.axis=="s"||o.axis=="e");
								 
        var mod = (e[isth ? 'pageY' : 'pageX'] - o.startMousePosition[isth ? 'top' : 'left']) * (b ? -1 : 1);
        var val = o[ishw ? 'currentSize' : 'startPosition'][a] - mod - (!o.proxy && defAxis ? o.currentSizeDiff.width : 0);
				
				//Preserve ratio
        if (pRatio) {
					var v = val * Math.pow(o.aspectRatio, (isth ? -1 : 1) * (o.aspectRatioTarget == 'height' ? 1 : -1)), locked = false;
					
					if (isth && v >= o.maxWidth || !isth && v >= o.maxHeight)	locked = true;
					if (isth && v <= o.minWidth || !isth && v <= o.minHeight)	locked = true;
						
					if (ishw && !locked)	el.css(isth ? "width" : "height", v);
					
					if (a == "top" && (o.axis == "ne" || o.axis == "nw")) {
						//el.css('top', o.startPosition['top'] - (el.outerHeight() - o.currentSize.height) );
						/*TODO*/ return;
					};
				}
				el.css(a, val);
      };
      
			var a = o.axis, tminval = 0, tmaxval;
			
      //Change the height
      if(a=="n"||a=="ne"||a=="nw") change("height");
      if(a=="s"||a=="se"||a=="sw") change("height", 1);
      
      //Measure the new height and correct against min/maxHeight
			var curheight = parseInt(el.css('height'),10)||0;
      if(o.minHeight && curheight <= o.minHeight) el.css('height', o.minHeight);  
      if(o.maxHeight && curheight >= o.maxHeight) el.css('height', o.maxHeight);
      
      //Change the top position when picking a handle at north
      if(a=="n"||a=="ne"||a=="nw") change("top", 1);
	  
      //Measure the new top position and correct against min/maxHeight
			var curtop = parseInt(el.css('top'),10)||0;
			
			tminval = (o.startPosition.top + (o.currentSize.height - o.minHeight));
			tmaxval = (o.startPosition.top + (o.currentSize.height - o.maxHeight));
      if(o.minHeight && curtop >= tminval) el.css('top', tminval);
      if(o.maxHeight && curtop <= tmaxval) el.css('top', tmaxval);
		
      //Change the width
      if(a=="e"||a=="se"||a=="ne") change("width", 1);
      if(a=="sw"||a=="w"||a=="nw") change("width");
      
      //Measure the new width and correct against min/maxWidth
			var curwidth = parseInt(el.css('width'),10)||0;
      if(o.minWidth && curwidth <= o.minWidth) el.css('width', o.minWidth);  
      if(o.maxWidth && curwidth >= o.maxWidth) el.css('width', o.maxWidth);
      
      //Change the left position when picking a handle at west
      if(a=="sw"||a=="w"||a=="nw") change("left", 1);
      
      //Measure the new left position and correct against min/maxWidth
			var curleft = parseInt(el.css('left'),10)||0;
			
			tminval = (o.startPosition.left + (o.currentSize.width - o.minWidth));
			tmaxval = (o.startPosition.left + (o.currentSize.width - o.maxWidth));
      if(o.minWidth && curleft >= tminval) el.css('left', tminval);
      if(o.maxWidth && curleft <= tmaxval) el.css('left', tmaxval);
      
      if (o.containment && o.cdata.e) {
        if (curleft < 0) {
          el.css('left', 0);
          el.css('width', curwidth + curleft);
        }
        if (curtop < 0) {
          el.css('top', 0);
          el.css('height', curheight + curtop);
        }
        if (curwidth + o.currentSizeDiff.width + curleft >= o.cdata.w) 
					el.css('width', o.cdata.w - o.currentSizeDiff.width - (curleft < 0 ? 0 : curleft));
        if (curheight + o.currentSizeDiff.height + curtop >= o.cdata.h)
					el.css('height', o.cdata.h - o.currentSizeDiff.height - (curtop < 0 ? 0 : curtop));
      }
			o.currentPosition = { left: curleft, top: curtop };
      if (!o.proxy) this._proportionallyResize();
      this.propagate("resize", e);  
      return false;
    }
  });

})(jQuery);
(function($) {

	$.fn.extend({
		draggable: function(options) {
			var args = Array.prototype.slice.call(arguments, 1);
			
			return this.each(function() {
				if (typeof options == "string") {
					var drag = $.data(this, "ui-draggable");
					drag[options].apply(drag, args);

				} else if(!$.data(this, "ui-draggable"))
					new $.ui.draggable(this, options);
			});
		}
	});
	
	$.ui.draggable = function(element, options) {
		//Initialize needed constants
		var self = this;
		
		this.element = $(element);
		
		$.data(element, "ui-draggable", this);
		this.element.addClass("ui-draggable");
		
		//Prepare the passed options
		this.options = $.extend({}, options);
		var o = this.options;
		$.extend(o, {
			helper: o.ghosting == true ? 'clone' : (o.helper || 'original'),
			handle : o.handle ? ($(o.handle, element)[0] ? $(o.handle, element) : this.element) : this.element,
			appendTo: o.appendTo || 'parent'		
		});
		
		$(element).bind("setData.draggable", function(event, key, value){
			self.options[key] = value;
		}).bind("getData.draggable", function(event, key){
			return self.options[key];
		});
		
		//Initialize mouse events for interaction
		$(o.handle).mouseInteraction({
			executor: this,
			delay: o.delay,
			distance: o.distance || 0,
			dragPrevention: o.prevention ? o.prevention.toLowerCase().split(',') : ['input','textarea','button','select','option'],
			start: this.start,
			stop: this.stop,
			drag: this.drag,
			condition: function(e) { return !(e.target.className.indexOf("ui-resizable-handle") != -1 || this.disabled); }
		});
		
		//Position the node
		if(o.helper == 'original' && (this.element.css('position') == 'static' || this.element.css('position') == ''))
			this.element.css('position', 'relative');
		
	};
	
	$.extend($.ui.draggable.prototype, {
		plugins: {},
		ui: function(e) {
			return {
				helper: this.helper,
				position: this.position,
				absolutePosition: this.positionAbs,
				instance: this,
				options: this.options					
			};
		},
		propagate: function(n,e) {
			$.ui.plugin.call(this, n, [e, this.ui()]);
			return this.element.triggerHandler(n == "drag" ? n : "drag"+n, [e, this.ui()], this.options[n]);
		},
		destroy: function() {
			this.handle.removeMouseInteraction();
			this.element
				.removeClass("ui-draggable ui-draggable-disabled")
				.removeData("ui-draggable")
				.unbind(".draggable");
		},
		enable: function() {
			this.element.removeClass("ui-draggable-disabled");
			this.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-draggable-disabled");
			this.disabled = true;
		},
		recallOffset: function(e) {

			var elementPosition = { left: this.elementOffset.left - this.offsetParentOffset.left, top: this.elementOffset.top - this.offsetParentOffset.top };
			var r = this.helper.css('position') == 'relative';

			//Generate the original position
			this.originalPosition = {
				left: (r ? parseInt(this.helper.css('left'),10) || 0 : elementPosition.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft)),
				top: (r ? parseInt(this.helper.css('top'),10) || 0 : elementPosition.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop))
			};
			
			//Generate a flexible offset that will later be subtracted from e.pageX/Y
			this.offset = {left: this._pageX - this.originalPosition.left, top: this._pageY - this.originalPosition.top };
			
		},
		start: function(e) {
			
			var o = this.options;
			if($.ui.ddmanager) $.ui.ddmanager.current = this;
			
			//Create and append the visible helper
			this.helper = typeof o.helper == 'function' ? $(o.helper.apply(this.element[0], [e])) : (o.helper == 'clone' ? this.element.clone().appendTo((o.appendTo == 'parent' ? this.element[0].parentNode : o.appendTo)) : this.element);
			if(this.helper[0] != this.element[0]) this.helper.css('position', 'absolute');
			if(!this.helper.parents('body').length) this.helper.appendTo((o.appendTo == 'parent' ? this.element[0].parentNode : o.appendTo));

			//Find out the next positioned parent
			this.offsetParent = (function(cp) {
				while(cp) {
					if(cp.style && (/(absolute|relative|fixed)/).test($.css(cp,'position'))) return $(cp);
					cp = cp.parentNode ? cp.parentNode : null;
				}; return $("body");		
			})(this.helper[0].parentNode);
			
			//Prepare variables for position generation
			this.elementOffset = this.element.offset();
			this.offsetParentOffset = this.offsetParent.offset();
			var elementPosition = { left: this.elementOffset.left - this.offsetParentOffset.left, top: this.elementOffset.top - this.offsetParentOffset.top };
			this._pageX = e.pageX; this._pageY = e.pageY;
			this.clickOffset = { left: e.pageX - this.elementOffset.left, top: e.pageY - this.elementOffset.top };
			var r = this.helper.css('position') == 'relative';

			//Generate the original position
			this.originalPosition = {
				left: (r ? parseInt(this.helper.css('left'),10) || 0 : elementPosition.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft)),
				top: (r ? parseInt(this.helper.css('top'),10) || 0 : elementPosition.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop))
			};
			
			//If we have a fixed element, we must subtract the scroll offset again
			if(this.element.css('position') == 'fixed') {
				this.originalPosition.top -= this.offsetParent[0] == document.body ? $(document).scrollTop() : this.offsetParent[0].scrollTop;
				this.originalPosition.left -= this.offsetParent[0] == document.body ? $(document).scrollLeft() : this.offsetParent[0].scrollLeft;
			}
			
			//Generate a flexible offset that will later be subtracted from e.pageX/Y
			this.offset = {left: e.pageX - this.originalPosition.left, top: e.pageY - this.originalPosition.top };
			
			//Call plugins and callbacks
			this.propagate("start", e);

			this.helperProportions = { width: this.helper.outerWidth(), height: this.helper.outerHeight() };
			if ($.ui.ddmanager && !o.dropBehaviour) $.ui.ddmanager.prepareOffsets(this, e);
			
			//If we have something in cursorAt, we'll use it
			if(o.cursorAt) {
				if(o.cursorAt.top != undefined || o.cursorAt.bottom != undefined) {
					this.offset.top -= this.clickOffset.top - (o.cursorAt.top != undefined ? o.cursorAt.top : (this.helperProportions.height - o.cursorAt.bottom));
					this.clickOffset.top = (o.cursorAt.top != undefined ? o.cursorAt.top : (this.helperProportions.height - o.cursorAt.bottom));
				}
				if(o.cursorAt.left != undefined || o.cursorAt.right != undefined) {
					this.offset.left -= this.clickOffset.left - (o.cursorAt.left != undefined ? o.cursorAt.left : (this.helperProportions.width - o.cursorAt.right));
					this.clickOffset.left = (o.cursorAt.left != undefined ? o.cursorAt.left : (this.helperProportions.width - o.cursorAt.right));
				}
			}

			return false;

		},
		clear: function() {
			if($.ui.ddmanager) $.ui.ddmanager.current = null;
			this.helper = null;
		},
		stop: function(e) {

			//If we are using droppables, inform the manager about the drop
			if ($.ui.ddmanager && !this.options.dropBehaviour)
				$.ui.ddmanager.drop(this, e);
				
			//Call plugins and trigger callbacks
			this.propagate("stop", e);
			
			if(this.cancelHelperRemoval) return false;			
			if(this.options.helper != 'original') this.helper.remove();
			this.clear();

			return false;
		},
		drag: function(e) {

			//Compute the helpers position
			this.position = { top: e.pageY - this.offset.top, left: e.pageX - this.offset.left };
			this.positionAbs = { left: e.pageX - this.clickOffset.left, top: e.pageY - this.clickOffset.top };

			//Call plugins and callbacks			
			this.position = this.propagate("drag", e) || this.position;
			
			this.helper.css({ left: this.position.left+'px', top: this.position.top+'px' }); // Stick the helper to the cursor
			if($.ui.ddmanager) $.ui.ddmanager.drag(this, e);
			return false;
			
		}
	});

})(jQuery);
/*
 * 'this' -> original element
 * 1. argument: browser event
 * 2.argument: ui object
 */

(function($) {

	$.ui.plugin.add("draggable", "cursor", {
		start: function(e,ui) {
			var t = $('body');
			if (t.css("cursor")) ui.options._cursor = t.css("cursor");
			t.css("cursor", ui.options.cursor);
		},
		stop: function(e,ui) {
			if (ui.options._cursor) $('body').css("cursor", ui.options._cursor);
		}
	});

	$.ui.plugin.add("draggable", "zIndex", {
		start: function(e,ui) {
			var t = $(ui.helper);
			if(t.css("zIndex")) ui.options._zIndex = t.css("zIndex");
			t.css('zIndex', ui.options.zIndex);
		},
		stop: function(e,ui) {
			if(ui.options._zIndex) $(ui.helper).css('zIndex', ui.options._zIndex);
		}
	});

	$.ui.plugin.add("draggable", "opacity", {
		start: function(e,ui) {
			var t = $(ui.helper);
			if(t.css("opacity")) ui.options._opacity = t.css("opacity");
			t.css('opacity', ui.options.opacity);
		},
		stop: function(e,ui) {
			if(ui.options._opacity) $(ui.helper).css('opacity', ui.options._opacity);
		}
	});


	$.ui.plugin.add("draggable", "revert", {
		stop: function(e,ui) {
			var self = ui.instance;
			self.cancelHelperRemoval = true;
			$(ui.helper).animate({ left: self.originalPosition.left, top: self.originalPosition.top }, parseInt(ui.options.revert, 10) || 500, function() {
				if(ui.options.helper != 'original') self.helper.remove();
				self.clear();
			});
		}
	});

	$.ui.plugin.add("draggable", "iframeFix", {
		start: function(e,ui) {

			var o = ui.options;
			if(ui.instance.slowMode) return; // Make clones on top of iframes (only if we are not in slowMode)
			
			if(o.iframeFix.constructor == Array) {
				for(var i=0;i<o.iframeFix.length;i++) {
					var co = $(o.iframeFix[i]).offset({ border: false });
					$('<div class="DragDropIframeFix"" style="background: #fff;"></div>').css("width", $(o.iframeFix[i])[0].offsetWidth+"px").css("height", $(o.iframeFix[i])[0].offsetHeight+"px").css("position", "absolute").css("opacity", "0.001").css("z-index", "1000").css("top", co.top+"px").css("left", co.left+"px").appendTo("body");
				}		
			} else {
				$("iframe").each(function() {					
					var co = $(this).offset({ border: false });
					$('<div class="DragDropIframeFix" style="background: #fff;"></div>').css("width", this.offsetWidth+"px").css("height", this.offsetHeight+"px").css("position", "absolute").css("opacity", "0.001").css("z-index", "1000").css("top", co.top+"px").css("left", co.left+"px").appendTo("body");
				});							
			}

		},
		stop: function(e,ui) {
			if(ui.options.iframeFix) $("div.DragDropIframeFix").each(function() { this.parentNode.removeChild(this); }); //Remove frame helpers	
		}
	});
	
	$.ui.plugin.add("draggable", "containment", {
		start: function(e,ui) {

			var o = ui.options;
			if((o.containment.left != undefined || o.containment.constructor == Array) && !o._containment) return;
			if(!o._containment) o._containment = o.containment;

			if(o._containment == 'parent') o._containment = this[0].parentNode;
			if(o._containment == 'document') {
				o.containment = [
					0,
					0,
					$(document).width(),
					($(document).height() || document.body.parentNode.scrollHeight)
				];
			} else { //I'm a node, so compute top/left/right/bottom

				var ce = $(o._containment)[0];
				var co = $(o._containment).offset();

				o.containment = [
					co.left,
					co.top,
					co.left+(ce.offsetWidth || ce.scrollWidth),
					co.top+(ce.offsetHeight || ce.scrollHeight)
				];
			}

		},
		drag: function(e,ui) {

			var o = ui.options;
			var h = ui.helper;
			var c = o.containment;
			var self = ui.instance;
			
			if(c.constructor == Array) {
				if((ui.absolutePosition.left < c[0])) self.position.left = c[0] - (self.offset.left - self.clickOffset.left);
				if((ui.absolutePosition.top < c[1])) self.position.top = c[1] - (self.offset.top - self.clickOffset.top);
				if(ui.absolutePosition.left - c[2] + self.helperProportions.width >= 0) self.position.left = c[2] - (self.offset.left - self.clickOffset.left) - self.helperProportions.width;
				if(ui.absolutePosition.top - c[3] + self.helperProportions.height >= 0) self.position.top = c[3] - (self.offset.top - self.clickOffset.top) - self.helperProportions.height;
			} else {
				if((ui.position.left < c.left)) self.position.left = c.left;
				if((ui.position.top < c.top)) self.position.top = c.top;
				if(ui.position.left - self.offsetParent.innerWidth() + self.helperProportions.width + c.right + (parseInt(self.offsetParent.css("borderLeftWidth"), 10) || 0) + (parseInt(self.offsetParent.css("borderRightWidth"), 10) || 0) >= 0) self.position.left = self.offsetParent.innerWidth() - self.helperProportions.width - c.right - (parseInt(self.offsetParent.css("borderLeftWidth"), 10) || 0) - (parseInt(self.offsetParent.css("borderRightWidth"), 10) || 0);
				if(ui.position.top - self.offsetParent.innerHeight() + self.helperProportions.height + c.bottom + (parseInt(self.offsetParent.css("borderTopWidth"), 10) || 0) + (parseInt(self.offsetParent.css("borderBottomWidth"), 10) || 0) >= 0) self.position.top = self.offsetParent.innerHeight() - self.helperProportions.height - c.bottom - (parseInt(self.offsetParent.css("borderTopWidth"), 10) || 0) - (parseInt(self.offsetParent.css("borderBottomWidth"), 10) || 0);
			}

		}
	});

	$.ui.plugin.add("draggable", "grid", {
		drag: function(e,ui) {
			var o = ui.options;
			ui.instance.position.left = ui.instance.originalPosition.left + Math.round((e.pageX - ui.instance._pageX) / o.grid[0]) * o.grid[0];
			ui.instance.position.top = ui.instance.originalPosition.top + Math.round((e.pageY - ui.instance._pageY) / o.grid[1]) * o.grid[1];
		}
	});

	$.ui.plugin.add("draggable", "axis", {
		drag: function(e,ui) {
			var o = ui.options;
			if(o.constraint) o.axis = o.constraint; //Legacy check
			o.axis == 'x' ? ui.instance.position.top = ui.instance.originalPosition.top : ui.instance.position.left = ui.instance.originalPosition.left;
		}
	});

	$.ui.plugin.add("draggable", "scroll", {
		start: function(e,ui) {
			var o = ui.options;
			o.scrollSensitivity	= o.scrollSensitivity || 20;
			o.scrollSpeed		= o.scrollSpeed || 20;

			ui.instance.overflowY = function(el) {
				do { if(/auto|scroll/.test(el.css('overflow')) || (/auto|scroll/).test(el.css('overflow-y'))) return el; el = el.parent(); } while (el[0].parentNode);
				return $(document);
			}(this);
			ui.instance.overflowX = function(el) {
				do { if(/auto|scroll/.test(el.css('overflow')) || (/auto|scroll/).test(el.css('overflow-x'))) return el; el = el.parent(); } while (el[0].parentNode);
				return $(document);
			}(this);
		},
		drag: function(e,ui) {
			
			var o = ui.options;
			var i = ui.instance;

			if(i.overflowY[0] != document && i.overflowY[0].tagName != 'HTML') {
				if(i.overflowY[0].offsetHeight - (ui.position.top - i.overflowY[0].scrollTop + i.clickOffset.top) < o.scrollSensitivity)
					i.overflowY[0].scrollTop = i.overflowY[0].scrollTop + o.scrollSpeed;
				if((ui.position.top - i.overflowY[0].scrollTop + i.clickOffset.top) < o.scrollSensitivity)
					i.overflowY[0].scrollTop = i.overflowY[0].scrollTop - o.scrollSpeed;				
			} else {
				//$(document.body).append('<p>'+(e.pageY - $(document).scrollTop())+'</p>');
				if(e.pageY - $(document).scrollTop() < o.scrollSensitivity)
					$(document).scrollTop($(document).scrollTop() - o.scrollSpeed);
				if($(window).height() - (e.pageY - $(document).scrollTop()) < o.scrollSensitivity)
					$(document).scrollTop($(document).scrollTop() + o.scrollSpeed);
			}
			
			if(i.overflowX[0] != document && i.overflowX[0].tagName != 'HTML') {
				if(i.overflowX[0].offsetWidth - (ui.position.left - i.overflowX[0].scrollLeft + i.clickOffset.left) < o.scrollSensitivity)
					i.overflowX[0].scrollLeft = i.overflowX[0].scrollLeft + o.scrollSpeed;
				if((ui.position.top - i.overflowX[0].scrollLeft + i.clickOffset.left) < o.scrollSensitivity)
					i.overflowX[0].scrollLeft = i.overflowX[0].scrollLeft - o.scrollSpeed;				
			} else {
				if(e.pageX - $(document).scrollLeft() < o.scrollSensitivity)
					$(document).scrollLeft($(document).scrollLeft() - o.scrollSpeed);
				if($(window).width() - (e.pageX - $(document).scrollLeft()) < o.scrollSensitivity)
					$(document).scrollLeft($(document).scrollLeft() + o.scrollSpeed);
			}
			
			ui.instance.recallOffset(e);

		}
	});
	
	$.ui.plugin.add("draggable", "snap", {
		start: function(e,ui) {
			
			ui.instance.snapElements = [];
			$(ui.options.snap === true ? '.ui-draggable' : ui.options.snap).each(function() {
				var $t = $(this); var $o = $t.offset();
				if(this != ui.instance.element[0]) ui.instance.snapElements.push({
					item: this,
					width: $t.outerWidth(),
					height: $t.outerHeight(),
					top: $o.top,
					left: $o.left
				});
			});
			
		},
		drag: function(e,ui) {

			var d = ui.options.snapTolerance || 20;
			var x1 = ui.absolutePosition.left, x2 = x1 + ui.instance.helperProportions.width,
			    y1 = ui.absolutePosition.top, y2 = y1 + ui.instance.helperProportions.height;

			for (var i = ui.instance.snapElements.length - 1; i >= 0; i--){

				var l = ui.instance.snapElements[i].left, r = l + ui.instance.snapElements[i].width, 
				    t = ui.instance.snapElements[i].top,  b = t + ui.instance.snapElements[i].height;

				//Yes, I know, this is insane ;)
				if(!((l-d < x1 && x1 < r+d && t-d < y1 && y1 < b+d) || (l-d < x1 && x1 < r+d && t-d < y2 && y2 < b+d) || (l-d < x2 && x2 < r+d && t-d < y1 && y1 < b+d) || (l-d < x2 && x2 < r+d && t-d < y2 && y2 < b+d))) continue;

				if(ui.options.snapMode != 'inner') {
					var ts = Math.abs(t - y2) <= 20;
					var bs = Math.abs(b - y1) <= 20;
					var ls = Math.abs(l - x2) <= 20;
					var rs = Math.abs(r - x1) <= 20;
					if(ts) ui.position.top = t - ui.instance.offset.top + ui.instance.clickOffset.top - ui.instance.helperProportions.height;
					if(bs) ui.position.top = b - ui.instance.offset.top + ui.instance.clickOffset.top;
					if(ls) ui.position.left = l - ui.instance.offset.left + ui.instance.clickOffset.left - ui.instance.helperProportions.width;
					if(rs) ui.position.left = r - ui.instance.offset.left + ui.instance.clickOffset.left;
				}
				
				if(ui.options.snapMode != 'outer') {
					var ts = Math.abs(t - y1) <= 20;
					var bs = Math.abs(b - y2) <= 20;
					var ls = Math.abs(l - x1) <= 20;
					var rs = Math.abs(r - x2) <= 20;
					if(ts) ui.position.top = t - ui.instance.offset.top + ui.instance.clickOffset.top;
					if(bs) ui.position.top = b - ui.instance.offset.top + ui.instance.clickOffset.top - ui.instance.helperProportions.height;
					if(ls) ui.position.left = l - ui.instance.offset.left + ui.instance.clickOffset.left;
					if(rs) ui.position.left = r - ui.instance.offset.left + ui.instance.clickOffset.left - ui.instance.helperProportions.width;
				}

			};
		}
	});

	//TODO: wrapHelper, snap

})(jQuery);

(function($) {

	$.fn.extend({
		droppable: function(options) {
			var args = Array.prototype.slice.call(arguments, 1);
			
			return this.each(function() {
				if (typeof options == "string") {
					var drop = $.data(this, "ui-droppable");
					drop[options].apply(drop, args);

				} else if(!$.data(this, "ui-droppable"))
					new $.ui.droppable(this, options);
			});
		}
	});

	
	$.ui.droppable = function(element, options) {

		//Initialize needed constants			
		this.element = $(element);
		$.data(element, "ui-droppable", this);
		this.element.addClass("ui-droppable");		
		
		//Prepare the passed options
		this.options = $.extend({}, options);
		var o = this.options; var accept = o.accept;
		$.extend(o, {
			accept: o.accept && o.accept.constructor == Function ? o.accept : function(d) {
				return $(d).is(accept);	
			},
			tolerance: o.tolerance || 'intersect'		
		});
		
		$(element).bind("setData.draggable", function(event, key, value){
			o[key] = value;
		}).bind("getData.draggable", function(event, key){
			return o[key];
		});
		
		//Store the droppable's proportions
		this.proportions = { width: this.element.outerWidth(), height: this.element.outerHeight() };
		
		// Add the reference and positions to the manager
		$.ui.ddmanager.droppables.push({ item: this, over: 0, out: 1 });
			
	};
	
	$.extend($.ui.droppable.prototype, {
		plugins: {},
		ui: function(c) {
			return {
				instance: this,
				draggable: c.element,
				helper: c.helper,
				position: c.position,
				absolutePosition: c.positionAbs,
				options: this.options	
			};		
		},
		destroy: function() {
			var drop = $.ui.ddmanager.droppables;
			for ( var i = 0; i < drop.length; i++ )
				if ( drop[i].item == this )
					drop.splice(i, 1);
			
			this.element
				.removeClass("ui-droppable ui-droppable-disabled")
				.removeData("ui-droppable")
				.unbind(".droppable");
		},
		enable: function() {
			this.element.removeClass("ui-droppable-disabled");
			this.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-droppable-disabled");
			this.disabled = true;
		},
		over: function(e) {

			var draggable = $.ui.ddmanager.current;
			if (!draggable || draggable.element[0] == this.element[0]) return; // Bail if draggable and droppable are same element
			
			if (this.options.accept.call(this.element,draggable.element)) {
				$.ui.plugin.call(this, 'over', [e, this.ui(draggable)]);
				this.element.triggerHandler("dropover", [e, this.ui(draggable)], this.options.over);
			}
			
		},
		out: function(e) {

			var draggable = $.ui.ddmanager.current;
			if (!draggable || draggable.element[0] == this.element[0]) return; // Bail if draggable and droppable are same element

			if (this.options.accept.call(this.element,draggable.element)) {
				$.ui.plugin.call(this, 'out', [e, this.ui(draggable)]);
				this.element.triggerHandler("dropout", [e, this.ui(draggable)], this.options.out);
			}
			
		},
		drop: function(e) {

			var draggable = $.ui.ddmanager.current;
			if (!draggable || draggable.element[0] == this.element[0]) return; // Bail if draggable and droppable are same element
			
			if(this.options.accept.call(this.element,draggable.element)) {
				$.ui.plugin.call(this, 'drop', [e, this.ui(draggable)]);
				this.element.triggerHandler("drop", [e, this.ui(draggable)], this.options.drop);
			}
			
		},
		activate: function(e) {

			var draggable = $.ui.ddmanager.current;
			$.ui.plugin.call(this, 'activate', [e, this.ui(draggable)]);
			if(draggable) this.element.triggerHandler("dropactivate", [e, this.ui(draggable)], this.options.activate);
				
		},
		deactivate: function(e) {
			
			var draggable = $.ui.ddmanager.current;
			$.ui.plugin.call(this, 'deactivate', [e, this.ui(draggable)]);
			if(draggable) this.element.triggerHandler("dropdeactivate", [e, this.ui(draggable)], this.options.deactivate);
			
		}
	});
	
	$.ui.intersect = function(draggable, droppable, toleranceMode) {

		if (!droppable.offset) return false;
		
		var x1 = draggable.positionAbs.left, x2 = x1 + draggable.helperProportions.width,
		    y1 = draggable.positionAbs.top, y2 = y1 + draggable.helperProportions.height;
		var l = droppable.offset.left, r = l + droppable.item.proportions.width, 
		    t = droppable.offset.top,  b = t + droppable.item.proportions.height;

		switch (toleranceMode) {
			case 'fit':
				return (   l < x1 && x2 < r
					&& t < y1 && y2 < b);
				break;
			case 'intersect':
				return (   l < x1 + (draggable.helperProportions.width  / 2)        // Right Half
					&&     x2 - (draggable.helperProportions.width  / 2) < r    // Left Half
					&& t < y1 + (draggable.helperProportions.height / 2)        // Bottom Half
					&&     y2 - (draggable.helperProportions.height / 2) < b ); // Top Half
				break;
			case 'pointer':
				return (   l < (draggable.positionAbs.left + draggable.clickOffset.left) && (draggable.positionAbs.left + draggable.clickOffset.left) < r
					&& t < (draggable.positionAbs.top + draggable.clickOffset.top) && (draggable.positionAbs.top + draggable.clickOffset.top) < b);
				break;
			case 'touch':
				return (   (l < x1 && x1 < r && t < y1 && y1 < b)    // Top-Left Corner
					|| (l < x1 && x1 < r && t < y2 && y2 < b)    // Bottom-Left Corner
					|| (l < x2 && x2 < r && t < y1 && y1 < b)    // Top-Right Corner
					|| (l < x2 && x2 < r && t < y2 && y2 < b) ); // Bottom-Right Corner
				break;
			default:
				return false;
				break;
			}
		
	};
	
	/*
		This manager tracks offsets of draggables and droppables
	*/
	$.ui.ddmanager = {
		current: null,
		droppables: [],
		prepareOffsets: function(t, e) {

			var m = $.ui.ddmanager.droppables;
			for (var i = 0; i < m.length; i++) {
				
				if(m[i].item.disabled || (t && !m[i].item.options.accept.call(m[i].item.element,t.element))) continue;
				m[i].offset = $(m[i].item.element).offset();
				
				if(t) m[i].item.activate.call(m[i].item, e); //Activate the droppable if used directly from draggables
					
			}
			
		},
		drop: function(draggable, e) {
			
			$.each($.ui.ddmanager.droppables, function() {
				
				if (!this.item.disabled && $.ui.intersect(draggable, this, this.item.options.tolerance))
					this.item.drop.call(this.item, e);
					
				if (!this.item.disabled && this.item.options.accept.call(this.item.element,draggable.element)) {
					this.out = 1; this.over = 0;
					this.item.deactivate.call(this.item, e);
				}
				
			});
			
		},
		drag: function(draggable, e) {
			
			//If you have a highly dynamic page, you might try this option. It renders positions every time you move the mouse.
			if(draggable.options.refreshPositions) $.ui.ddmanager.prepareOffsets();
		
			//Run through all draggables and check their positions based on specific tolerance options
			$.each($.ui.ddmanager.droppables, function() {

				if(this.item.disabled) return false; 
				var intersects = $.ui.intersect(draggable, this, this.item.options.tolerance);

				var c = !intersects && this.over == 1 ? 'out' : (intersects && this.over == 0 ? 'over' : null);
				if(!c) return;
					
				this[c] = 1; this[c == 'out' ? 'over' : 'out'] = 0;
				this.item[c].call(this.item, e);
					
			});
			
		}
	};
	
})(jQuery);

(function($) {

	$.ui.plugin.add("droppable", "activeClass", {
		activate: function(e,ui) {
			$(this).addClass(ui.options.activeClass);
		},
		deactivate: function(e,ui) {
			$(this).removeClass(ui.options.activeClass);
		},
		drop: function(e,ui) {
			$(this).removeClass(ui.options.activeClass);
		}
	});

	$.ui.plugin.add("droppable", "hoverClass", {
		over: function(e,ui) {
			$(this).addClass(ui.options.hoverClass);
		},
		out: function(e,ui) {
			$(this).removeClass(ui.options.hoverClass);
		},
		drop: function(e,ui) {
			$(this).removeClass(ui.options.hoverClass);
		}
	});	

})(jQuery);
(function($) {

	if (window.Node && Node.prototype && !Node.prototype.contains) {
		Node.prototype.contains = function (arg) {
			return !!(this.compareDocumentPosition(arg) & 16);
		};
	}

	$.fn.extend({
		sortable: function(options) {
			
			var args = Array.prototype.slice.call(arguments, 1);
			
			if ( options == "serialize" )
				return $.data(this[0], "ui-sortable").serialize(arguments[1]);
			
			return this.each(function() {
				if (typeof options == "string") {
					var sort = $.data(this, "ui-sortable");
					sort[options].apply(sort, args);

				} else if(!$.data(this, "ui-sortable"))
					new $.ui.sortable(this, options);
			});
		}
	});
	
	$.ui.sortable = function(element, options) {
		//Initialize needed constants
		var self = this;
		
		this.element = $(element);
		
		$.data(element, "ui-sortable", this);
		this.element.addClass("ui-sortable");

		//Prepare the passed options
		this.options = $.extend({}, options);
		var o = this.options;
		$.extend(o, {
			items: this.options.items || '> *',
			zIndex: this.options.zIndex || 1000,
			startCondition: function() {
				return !self.disabled;	
			}		
		});
		
		$(element).bind("setData.sortable", function(event, key, value){
			self.options[key] = value;
		}).bind("getData.sortable", function(event, key){
			return self.options[key];
		});
		
		//Get the items
		this.refresh();

		//Let's determine if the items are floating
		this.floating = /left|right/.test(this.items[0].item.css('float'));
		
		//Let's determine the parent's offset
		if(!(/(relative|absolute|fixed)/).test(this.element.css('position'))) this.element.css('position', 'relative');
		this.offset = this.element.offset({ border: false });

		//Initialize mouse events for interaction
		this.element.mouseInteraction({
			executor: this,
			delay: o.delay,
			distance: o.distance || 0,
			dragPrevention: o.prevention ? o.prevention.toLowerCase().split(',') : ['input','textarea','button','select','option'],
			start: this.start,
			stop: this.stop,
			drag: this.drag,
			condition: function(e) {

				if(this.disabled) return false;

				//Find out if the clicked node (or one of its parents) is a actual item in this.items
				var currentItem = null, nodes = $(e.target).parents().andSelf().each(function() {
					if($.data(this, 'ui-sortable-item')) currentItem = $(this);
				});
				if(currentItem && (!this.options.handle || $(e.target).parents().andSelf().is(this.options.handle))) {
					this.currentItem = currentItem;
					return true;
				} else return false; 

			}
		});

	};
	
	$.extend($.ui.sortable.prototype, {
		plugins: {},
		ui: function() {
			return {
				helper: this.helper,
				placeholder: this.placeholder || $([]),
				position: this.position,
				absolutePosition: this.positionAbs,
				instance: this,
				options: this.options
			};		
		},
		propagate: function(n,e) {
			$.ui.plugin.call(this, n, [e, this.ui()]);
			this.element.triggerHandler(n == "sort" ? n : "sort"+n, [e, this.ui()], this.options[n]);
		},
		serialize: function(o) {
			
			var items = $(this.options.items, this.element).not('.ui-sortable-helper'); //Only the items of the sortable itself
			var str = [];
			o = o || {};
			
			items.each(function() {
				var res = (this.getAttribute(o.attribute || 'id') || '').match(o.expression || (/(.+)[-=_](.+)/));
				if(res) str.push((o.key || res[1])+'[]='+(o.key ? res[1] : res[2]));				
			});
			
			return str.join('&');
			
		},
		intersectsWith: function(item) {
			
			var x1 = this.positionAbs.left, x2 = x1 + this.helperProportions.width,
			    y1 = this.positionAbs.top, y2 = y1 + this.helperProportions.height;
			var l = item.left, r = l + item.width, 
			    t = item.top,  b = t + item.height;
			
			return (   l < x1 + (this.helperProportions.width  / 2)        // Right Half
				&&     x2 - (this.helperProportions.width  / 2) < r    // Left Half
				&& t < y1 + (this.helperProportions.height / 2)        // Bottom Half
				&&     y2 - (this.helperProportions.height / 2) < b ); // Top Half
			
		},
		refresh: function() {
			
			this.items = [];
			var items = this.items;
			var queries = [$(this.options.items, this.element)];
			
			if(this.options.connectWith) {
				for (var i = this.options.connectWith.length - 1; i >= 0; i--){
					var inst = $.data($(this.options.connectWith[i])[0], 'ui-sortable');
					if(inst && !inst.disabled) queries.push($(inst.options.items, inst.element));
				};
			}
			
			for (var i = queries.length - 1; i >= 0; i--){
				queries[i].each(function() {
					$.data(this, 'ui-sortable-item', true); // Data for target checking (mouse manager)
					items.push({
						item: $(this),
						width: 0, height: 0,
						left: 0, top: 0
					});
				});
			};

		},
		refreshPositions: function(fast) {
			for (var i = this.items.length - 1; i >= 0; i--){
				if(!fast) this.items[i].width = this.items[i].item.outerWidth();
				if(!fast) this.items[i].height = this.items[i].item.outerHeight();
				var p = this.items[i].item.offset();
				this.items[i].left = p.left;
				this.items[i].top = p.top;
			};
		},
		destroy: function() {
			this.element
				.removeClass("ui-sortable ui-sortable-disabled")
				.removeData("ui-sortable")
				.unbind(".sortable")
				.removeMouseInteraction();
			
			for ( var i = this.items.length - 1; i >= 0; i-- )
				this.items[i].item.removeData("ui-sortable-item");
		},
		enable: function() {
			this.element.removeClass("ui-sortable-disabled");
			this.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-sortable-disabled");
			this.disabled = true;
		},
		createPlaceholder: function() {
			this.placeholderElement = this.options.placeholderElement ? $(this.options.placeholderElement, this.currentItem) : this.currentItem;
			this.placeholder = $('<div></div>')
				.addClass(this.options.placeholder)
				.appendTo('body')
				.css({ position: 'absolute' })
				.css(this.placeholderElement.offset())
				.css({ width: this.placeholderElement.outerWidth(), height: this.placeholderElement.outerHeight() })
				;
		},
		recallOffset: function(e) {

			var elementPosition = { left: this.elementOffset.left - this.offsetParentOffset.left, top: this.elementOffset.top - this.offsetParentOffset.top };
			var r = this.helper.css('position') == 'relative';

			//Generate the original position
			this.originalPosition = {
				left: (r ? parseInt(this.helper.css('left'),10) || 0 : elementPosition.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft)),
				top: (r ? parseInt(this.helper.css('top'),10) || 0 : elementPosition.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop))
			};
			
			//Generate a flexible offset that will later be subtracted from e.pageX/Y
			this.offset = {
				left: this._pageX - this.originalPosition.left + (parseInt(this.currentItem.css('marginLeft'),10) || 0),
				top: this._pageY - this.originalPosition.top + (parseInt(this.currentItem.css('marginTop'),10) || 0)
			};
			
		},
		start: function(e) {
			
			var o = this.options;

			//Refresh the droppable items
			this.refresh(); this.refreshPositions();

			//Create and append the visible helper
			this.helper = typeof o.helper == 'function' ? $(o.helper.apply(this.element[0], [e, this.currentItem])) : this.currentItem.clone();
			this.helper.appendTo(this.currentItem[0].parentNode).css({ position: 'absolute', clear: 'both' }).addClass('ui-sortable-helper');

			//Find out the next positioned parent
			this.offsetParent = (function(cp) {
				while(cp) {
					if(cp.style && (/(absolute|relative|fixed)/).test($.css(cp,'position'))) return $(cp);
					cp = cp.parentNode ? cp.parentNode : null;
				}; return $("body");		
			})(this.helper[0].parentNode);
			
			//Prepare variables for position generation
			this.elementOffset = this.currentItem.offset();
			this.offsetParentOffset = this.offsetParent.offset();
			var elementPosition = { left: this.elementOffset.left - this.offsetParentOffset.left, top: this.elementOffset.top - this.offsetParentOffset.top };
			this._pageX = e.pageX; this._pageY = e.pageY;
			this.clickOffset = { left: e.pageX - this.elementOffset.left, top: e.pageY - this.elementOffset.top };
			var r = this.helper.css('position') == 'relative';
			
			//Generate the original position
			this.originalPosition = {
				left: (r ? parseInt(this.helper.css('left'),10) || 0 : elementPosition.left + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollLeft)),
				top: (r ? parseInt(this.helper.css('top'),10) || 0 : elementPosition.top + (this.offsetParent[0] == document.body ? 0 : this.offsetParent[0].scrollTop))
			};
			
			//Generate a flexible offset that will later be subtracted from e.pageX/Y
			//I hate margins - they need to be removed before positioning the element absolutely..
			this.offset = {
				left: e.pageX - this.originalPosition.left + (parseInt(this.currentItem.css('marginLeft'),10) || 0),
				top: e.pageY - this.originalPosition.top + (parseInt(this.currentItem.css('marginTop'),10) || 0)
			};

			//Save the first time position
			this.position = { top: e.pageY - this.offset.top, left: e.pageX - this.offset.left };
			this.positionAbs = { left: e.pageX - this.clickOffset.left, top: e.pageY - this.clickOffset.top };
			this.positionDOM = this.currentItem.prev()[0];

			//If o.placeholder is used, create a new element at the given position with the class
			if(o.placeholder) this.createPlaceholder();

			//Call plugins and callbacks
			this.propagate("start", e);

			//Save and store the helper proportions
			this.helperProportions = { width: this.helper.outerWidth(), height: this.helper.outerHeight() };
			
			//Set the original element visibility to hidden to still fill out the white space	
			$(this.currentItem).css('visibility', 'hidden');

			return false;
						
		},
		stop: function(e) {

			this.propagate("stop", e); //Call plugins and trigger callbacks
			if(this.positionDOM != this.currentItem.prev()[0]) this.propagate("update", e);
			
			if(this.cancelHelperRemoval) return false;			
			$(this.currentItem).css('visibility', '');
			if(this.placeholder) this.placeholder.remove();
			this.helper.remove();

			return false;
			
		},
		drag: function(e) {

			//Compute the helpers position
			this.direction = (this.floating && this.positionAbs.left > e.pageX - this.clickOffset.left) || (this.positionAbs.top > e.pageY - this.clickOffset.top) ? 'down' : 'up';
			this.position = { top: e.pageY - this.offset.top, left: e.pageX - this.offset.left };
			this.positionAbs = { left: e.pageX - this.clickOffset.left, top: e.pageY - this.clickOffset.top };

			//Rearrange
			for (var i = this.items.length - 1; i >= 0; i--) {
				if(this.intersectsWith(this.items[i]) && this.items[i].item[0] != this.currentItem[0] && (this.options.tree ? !this.currentItem[0].contains(this.items[i].item[0]) : true)) {
					//Rearrange the DOM
					this.items[i].item[this.direction == 'down' ? 'before' : 'after'](this.currentItem);
					this.refreshPositions(true); //Precompute after each DOM insertion, NOT on mousemove
					if(this.placeholderElement) this.placeholder.css(this.placeholderElement.offset());
					this.propagate("change", e); //Call plugins and callbacks
					break;
				}
			}

			this.propagate("sort", e); //Call plugins and callbacks
			this.helper.css({ left: this.position.left+'px', top: this.position.top+'px' }); // Stick the helper to the cursor
			return false;
			
		}
	});

})(jQuery);/*
 * 'this' -> original element
 * 1. argument: browser event
 * 2.argument: ui object
 */

(function($) {

	$.ui.plugin.add("sortable", "cursor", {
		start: function(e,ui) {
			var t = $('body');
			if (t.css("cursor")) ui.options._cursor = t.css("cursor");
			t.css("cursor", ui.options.cursor);
		},
		stop: function(e,ui) {
			if (ui.options._cursor) $('body').css("cursor", ui.options._cursor);
		}
	});

	$.ui.plugin.add("sortable", "zIndex", {
		start: function(e,ui) {
			var t = ui.helper;
			if(t.css("zIndex")) ui.options._zIndex = t.css("zIndex");
			t.css('zIndex', ui.options.zIndex);
		},
		stop: function(e,ui) {
			if(ui.options._zIndex) $(ui.helper).css('zIndex', ui.options._zIndex);
		}
	});

	$.ui.plugin.add("sortable", "opacity", {
		start: function(e,ui) {
			var t = ui.helper;
			if(t.css("opacity")) ui.options._opacity = t.css("opacity");
			t.css('opacity', ui.options.opacity);
		},
		stop: function(e,ui) {
			if(ui.options._opacity) $(ui.helper).css('opacity', ui.options._opacity);
		}
	});


	$.ui.plugin.add("sortable", "revert", {
		stop: function(e,ui) {
			var self = ui.instance;
			self.cancelHelperRemoval = true;
			var cur = self.currentItem.offset();
			if(ui.instance.options.zIndex) ui.helper.css('zIndex', ui.instance.options.zIndex); //Do the zIndex again because it already was resetted by the plugin above on stop

			//Also animate the placeholder if we have one
			if(ui.instance.placeholder) ui.instance.placeholder.animate({ opacity: 'hide' }, parseInt(ui.options.revert, 10) || 500);
			
			ui.helper.animate({
				left: cur.left - self.offsetParentOffset.left - (parseInt(self.currentItem.css('marginLeft'),10) || 0),
				top: cur.top - self.offsetParentOffset.top - (parseInt(self.currentItem.css('marginTop'),10) || 0)
			}, parseInt(ui.options.revert, 10) || 500, function() {
				self.currentItem.css('visibility', 'visible');
				window.setTimeout(function() {
					if(self.placeholder) self.placeholder.remove();
					self.helper.remove();
					if(ui.options._zIndex) ui.helper.css('zIndex', ui.options._zIndex);
				}, 50);
			});
		}
	});

	
	$.ui.plugin.add("sortable", "containment", {
		start: function(e,ui) {

			var o = ui.options;
			if((o.containment.left != undefined || o.containment.constructor == Array) && !o._containment) return;
			if(!o._containment) o._containment = o.containment;

			if(o._containment == 'parent') o._containment = this[0].parentNode;
			if(o._containment == 'document') {
				o.containment = [
					0,
					0,
					$(document).width(),
					($(document).height() || document.body.parentNode.scrollHeight)
				];
			} else { //I'm a node, so compute top/left/right/bottom

				var ce = $(o._containment)[0];
				var co = $(o._containment).offset();

				o.containment = [
					co.left,
					co.top,
					co.left+(ce.offsetWidth || ce.scrollWidth),
					co.top+(ce.offsetHeight || ce.scrollHeight)
				];
			}

		},
		sort: function(e,ui) {

			var o = ui.options;
			var h = ui.helper;
			var c = o.containment;
			var self = ui.instance;
			
			if(c.constructor == Array) {
				if((ui.absolutePosition.left < c[0])) self.position.left = c[0] - (self.offset.left - self.clickOffset.left);
				if((ui.absolutePosition.top < c[1])) self.position.top = c[1] - (self.offset.top - self.clickOffset.top);
				if(ui.absolutePosition.left - c[2] + self.helperProportions.width >= 0) self.position.left = c[2] - (self.offset.left - self.clickOffset.left) - self.helperProportions.width;
				if(ui.absolutePosition.top - c[3] + self.helperProportions.height >= 0) self.position.top = c[3] - (self.offset.top - self.clickOffset.top) - self.helperProportions.height;
			} else {
				if((ui.position.left < c.left)) self.position.left = c.left;
				if((ui.position.top < c.top)) self.position.top = c.top;
				if(ui.position.left - self.offsetParent.innerWidth() + self.helperProportions.width + c.right + (parseInt(self.offsetParent.css("borderLeftWidth"), 10) || 0) + (parseInt(self.offsetParent.css("borderRightWidth"), 10) || 0) >= 0) self.position.left = self.offsetParent.innerWidth() - self.helperProportions.width - c.right - (parseInt(self.offsetParent.css("borderLeftWidth"), 10) || 0) - (parseInt(self.offsetParent.css("borderRightWidth"), 10) || 0);
				if(ui.position.top - self.offsetParent.innerHeight() + self.helperProportions.height + c.bottom + (parseInt(self.offsetParent.css("borderTopWidth"), 10) || 0) + (parseInt(self.offsetParent.css("borderBottomWidth"), 10) || 0) >= 0) self.position.top = self.offsetParent.innerHeight() - self.helperProportions.height - c.bottom - (parseInt(self.offsetParent.css("borderTopWidth"), 10) || 0) - (parseInt(self.offsetParent.css("borderBottomWidth"), 10) || 0);
			}

		}
	});

	$.ui.plugin.add("sortable", "axis", {
		sort: function(e,ui) {
			var o = ui.options;
			if(o.constraint) o.axis = o.constraint; //Legacy check
			o.axis == 'x' ? ui.instance.position.top = ui.instance.originalPosition.top : ui.instance.position.left = ui.instance.originalPosition.left;
		}
	});

	$.ui.plugin.add("sortable", "scroll", {
		start: function(e,ui) {
			var o = ui.options;
			o.scrollSensitivity	= o.scrollSensitivity || 20;
			o.scrollSpeed		= o.scrollSpeed || 20;

			ui.instance.overflowY = function(el) {
				do { if((/auto|scroll/).test(el.css('overflow')) || (/auto|scroll/).test(el.css('overflow-y'))) return el; el = el.parent(); } while (el[0].parentNode);
				return $(document);
			}(this);
			ui.instance.overflowX = function(el) {
				do { if((/auto|scroll/).test(el.css('overflow')) || (/auto|scroll/).test(el.css('overflow-x'))) return el; el = el.parent(); } while (el[0].parentNode);
				return $(document);
			}(this);
		},
		sort: function(e,ui) {
			
			var o = ui.options;
			var i = ui.instance;

			if(i.overflowY[0] != document && i.overflowY[0].tagName != 'HTML') {
				if(i.overflowY[0].offsetHeight - (ui.position.top - i.overflowY[0].scrollTop + i.clickOffset.top) < o.scrollSensitivity)
					i.overflowY[0].scrollTop = i.overflowY[0].scrollTop + o.scrollSpeed;
				if((ui.position.top - i.overflowY[0].scrollTop + i.clickOffset.top) < o.scrollSensitivity)
					i.overflowY[0].scrollTop = i.overflowY[0].scrollTop - o.scrollSpeed;				
			} else {
				//$(document.body).append('<p>'+(e.pageY - $(document).scrollTop())+'</p>');
				if(e.pageY - $(document).scrollTop() < o.scrollSensitivity)
					$(document).scrollTop($(document).scrollTop() - o.scrollSpeed);
				if($(window).height() - (e.pageY - $(document).scrollTop()) < o.scrollSensitivity)
					$(document).scrollTop($(document).scrollTop() + o.scrollSpeed);
			}
			
			if(i.overflowX[0] != document && i.overflowX[0].tagName != 'HTML') {
				if(i.overflowX[0].offsetWidth - (ui.position.left - i.overflowX[0].scrollLeft + i.clickOffset.left) < o.scrollSensitivity)
					i.overflowX[0].scrollLeft = i.overflowX[0].scrollLeft + o.scrollSpeed;
				if((ui.position.top - i.overflowX[0].scrollLeft + i.clickOffset.left) < o.scrollSensitivity)
					i.overflowX[0].scrollLeft = i.overflowX[0].scrollLeft - o.scrollSpeed;				
			} else {
				if(e.pageX - $(document).scrollLeft() < o.scrollSensitivity)
					$(document).scrollLeft($(document).scrollLeft() - o.scrollSpeed);
				if($(window).width() - (e.pageX - $(document).scrollLeft()) < o.scrollSensitivity)
					$(document).scrollLeft($(document).scrollLeft() + o.scrollSpeed);
			}
			
			ui.instance.recallOffset(e);

		}
	});

})(jQuery);

(function($) {

	//If the UI scope is not available, add it
	$.ui = $.ui || {};

	$.fn.extend({
		selectable: function(options) {
			return this.each(function() {
				if (typeof options == "string") {
					var select = $.data(this, "ui-selectable");
					select[options].apply(select, args);

				} else if(!$.data(this, "ui-selectable"))
					new $.ui.selectable(this, options);
			});
		}
	});

	$.ui.selectable = function(element, options) {
		var instance = this;
		
		this.element = $(element);
		
		$.data(this.element, "ui-selectable", this);
		this.element.addClass("ui-selectable");
		
		this.options = $.extend({
			appendTo: 'body',
			autoRefresh: true,
			filter: '*',
			tolerance: 'touch'
		}, options);
		
		$(element).bind("setData.selectable", function(event, key, value){
			instance.options[key] = value;
		}).bind("getData.selectable", function(event, key){
			return instance.options[key];
		});
		
		this.dragged = false;

		// cache selectee children based on filter
		var selectees;
		this.refresh = function() {
			selectees = $(instance.options.filter, instance.element[0]);
			selectees.each(function() {
				var $this = $(this);
				var pos = $this.offset();
				$.data(this, "ui-selectee", {
					element: this,
					$element: $this,
					left: pos.left,
					top: pos.top,
					right: pos.left + $this.width(),
					bottom: pos.top + $this.height(),
					startselected: false,
					selected: $this.hasClass('ui-selected'),
					selecting: $this.hasClass('ui-selecting'),
					unselecting: $this.hasClass('ui-unselecting')
				});
			});
		};
		this.refresh();

		this.selectees = selectees.addClass("ui-selectee");

		//Initialize mouse interaction
		this.element.mouseInteraction({
			executor: this,
			appendTo: 'body',
			delay: 0,
			distance: 0,
			dragPrevention: ['input','textarea','button','select','option'],
			start: this.start,
			stop: this.stop,
			drag: this.drag
		});
		
		this.helper = $(document.createElement('div')).css({border:'1px dotted black'});
	};

	$.extend($.ui.selectable.prototype, {
		toggle: function() {
			if(this.disabled){
				this.enable();
			} else {
				this.disable();
			}
		},
		destroy: function() {
			this.element
				.removeClass("ui-selectable ui-selectable-disabled")
				.removeData("ui-selectable")
				.unbind(".selectable");
			this.removeMouseInteraction();
		},
		enable: function() {
			this.element.removeClass("ui-selectable-disabled");
			this.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-selectable-disabled");
			this.disabled = true;
		},
		start: function(ev, element) {
			
			this.opos = [ev.pageX, ev.pageY];
			
			if (this.disabled)
				return;

			var options = this.options;

			this.selectees = $(options.filter, element);

			// selectable START callback
			this.element.triggerHandler("selectablestart", [ev, {
				"selectable": element,
				"options": options
			}], options.start);

			$('body').append(this.helper);
			// position helper (lasso)
			this.helper.css({
				"z-index": 100,
				"position": "absolute",
				"left": ev.clientX,
				"top": ev.clientY,
				"width": 0,
				"height": 0
			});

			if (options.autoRefresh) {
				this.refresh();
			}

			this.selectees.filter('.ui-selected').each(function() {
				var selectee = $.data(this, "ui-selectee");
				selectee.startselected = true;
				if (!ev.ctrlKey) {
					selectee.$element.removeClass('ui-selected');
					selectee.selected = false;
					selectee.$element.addClass('ui-unselecting');
					selectee.unselecting = true;
					// selectable UNSELECTING callback
					$(this.element).triggerHandler("selectableunselecting", [ev, {
						selectable: element,
						unselecting: selectee.element,
						options: options
					}], options.unselecting);
				}
			});
		},
		drag: function(ev, element) {
			this.dragged = true;
			
			if (this.disabled)
				return;

			var options = this.options;

			var x1 = this.opos[0], y1 = this.opos[1], x2 = ev.pageX, y2 = ev.pageY;
			if (x1 > x2) { var tmp = x2; x2 = x1; x1 = tmp; }
			if (y1 > y2) { var tmp = y2; y2 = y1; y1 = tmp; }
			this.helper.css({left: x1, top: y1, width: x2-x1, height: y2-y1});

			this.selectees.each(function() {
				var selectee = $.data(this, "ui-selectee");
				//prevent helper from being selected if appendTo: selectable
				if (selectee.element == element)
					return;
				var hit = false;
				if (options.tolerance == 'touch') {
					hit = ( !(selectee.left > x2 || selectee.right < x1 || selectee.top > y2 || selectee.bottom < y1) );
				} else if (options.tolerance == 'fit') {
					hit = (selectee.left > x1 && selectee.right < x2 && selectee.top > y1 && selectee.bottom < y2);
				}

				if (hit) {
					// SELECT
					if (selectee.selected) {
						selectee.$element.removeClass('ui-selected');
						selectee.selected = false;
					}
					if (selectee.unselecting) {
						selectee.$element.removeClass('ui-unselecting');
						selectee.unselecting = false;
					}
					if (!selectee.selecting) {
						selectee.$element.addClass('ui-selecting');
						selectee.selecting = true;
						// selectable SELECTING callback
						$(this.element).triggerHandler("selectableselecting", [ev, {
							selectable: element,
							selecting: selectee.element,
							options: options
						}], options.selecting);
					}
				} else {
					// UNSELECT
					if (selectee.selecting) {
						if (ev.ctrlKey && selectee.startselected) {
							selectee.$element.removeClass('ui-selecting');
							selectee.selecting = false;
							selectee.$element.addClass('ui-selected');
							selectee.selected = true;
						} else {
							selectee.$element.removeClass('ui-selecting');
							selectee.selecting = false;
							if (selectee.startselected) {
								selectee.$element.addClass('ui-unselecting');
								selectee.unselecting = true;
							}
							// selectable UNSELECTING callback
							$(this.element).triggerHandler("selectableunselecting", [ev, {
								selectable: element,
								unselecting: selectee.element,
								options: options
							}], options.unselecting);
						}
					}
					if (selectee.selected) {
						if (!ev.ctrlKey && !selectee.startselected) {
							selectee.$element.removeClass('ui-selected');
							selectee.selected = false;

							selectee.$element.addClass('ui-unselecting');
							selectee.unselecting = true;
							// selectable UNSELECTING callback
							$(this.element).triggerHandler("selectableunselecting", [ev, {
								selectable: element,
								unselecting: selectee.element,
								options: options
							}], options.unselecting);
						}
					}
				}
			});
		},
		stop: function(ev, element) {
			this.dragged = false;
			
			var options = this.options;

			$('.ui-unselecting', this.element).each(function() {
				var selectee = $.data(this, "ui-selectee");
				selectee.$element.removeClass('ui-unselecting');
				selectee.unselecting = false;
				selectee.startselected = false;
				$(this.element).triggerHandler("selectableunselected", [ev, {
					selectable: element,
					unselected: selectee.element,
					options: options
				}], options.unselected);
			});
			$('.ui-selecting', this.element).each(function() {
				var selectee = $.data(this, "ui-selectee");
				selectee.$element.removeClass('ui-selecting').addClass('ui-selected');
				selectee.selecting = false;
				selectee.selected = true;
				selectee.startselected = true;
				$(this.element).triggerHandler("selectableselected", [ev, {
					selectable: element,
					selected: selectee.element,
					options: options
				}], options.selected);
			});
			$(this.element).triggerHandler("selectablestop", [ev, {
				selectable: element,
				options: this.options
			}], this.options.stop);
			
			this.helper.remove();
		}
	});
	
})(jQuery);/*
 * jQuery UI Accordion 1.5
 * 
 * Copyright (c) 2007 Jörn Zaefferer
 *
 * http://docs.jquery.com/UI/Accordion
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Revision: $Id: jquery.accordion.js 4529 2008-01-24 18:41:30Z joern.zaefferer $
 *
 */

;(function($) {
	
// If the UI scope is not available, add it
$.ui = $.ui || {};

$.fn.extend({
	accordion: function(options, data) {
		var args = Array.prototype.slice.call(arguments, 1);

		return this.each(function() {
			if (typeof options == "string") {
				var accordion = $.data(this, "ui-accordion");
				accordion[options].apply(accordion, args);
			// INIT with optional options
			} else if (!$(this).is(".ui-accordion"))
				$.data(this, "ui-accordion", new $.ui.accordion(this, options));
		});
	},
	// deprecated, use accordion("activate", index) instead
	activate: function(index) {
		return this.accordion("activate", index);
	}
});

$.ui.accordion = function(container, options) {
	
	// setup configuration
	this.options = options = $.extend({}, $.ui.accordion.defaults, options);
	this.element = container;
	
	$(container).addClass("ui-accordion");
	
	if ( options.navigation ) {
		var current = $(container).find("a").filter(options.navigationFilter);
		if ( current.length ) {
			if ( current.filter(options.header).length ) {
				options.active = current;
			} else {
				options.active = current.parent().parent().prev();
				current.addClass("current");
			}
		}
	}
	
	// calculate active if not specified, using the first header
	options.headers = $(container).find(options.header);
	options.active = findActive(options.headers, options.active);

	if ( options.fillSpace ) {
		var maxHeight = $(container).parent().height();
		options.headers.each(function() {
			maxHeight -= $(this).outerHeight();
		});
		var maxPadding = 0;
		options.headers.next().each(function() {
			maxPadding = Math.max(maxPadding, $(this).innerHeight() - $(this).height());
		}).height(maxHeight - maxPadding);
	} else if ( options.autoheight ) {
		var maxHeight = 0;
		options.headers.next().each(function() {
			maxHeight = Math.max(maxHeight, $(this).outerHeight());
		}).height(maxHeight);
	}

	options.headers
		.not(options.active || "")
		.next()
		.hide();
	options.active.parent().andSelf().addClass(options.selectedClass);
	
	if (options.event)
		$(container).bind((options.event || "") + ".ui-accordion", clickHandler);
};

$.ui.accordion.prototype = {
	activate: function(index) {
		// call clickHandler with custom event
		clickHandler.call(this.element, {
			target: findActive( this.options.headers, index )[0]
		});
	},
	
	enable: function() {
		this.options.disabled = false;
	},
	disable: function() {
		this.options.disabled = true;
	},
	destroy: function() {
		this.options.headers.next().css("display", "");
		if ( this.options.fillSpace || this.options.autoheight ) {
			this.options.headers.next().css("height", "");
		}
		$.removeData(this.element, "ui-accordion");
		$(this.element).removeClass("ui-accordion").unbind(".ui-accordion");
	}
};

function scopeCallback(callback, scope) {
	return function() {
		return callback.apply(scope, arguments);
	};
};

function completed(cancel) {
	// if removed while animated data can be empty
	if (!$.data(this, "ui-accordion"))
		return;
	var instance = $.data(this, "ui-accordion");
	var options = instance.options;
	options.running = cancel ? 0 : --options.running;
	if ( options.running )
		return;
	if ( options.clearStyle ) {
		options.toShow.add(options.toHide).css({
			height: "",
			overflow: ""
		});
	}
	$(this).triggerHandler("change.ui-accordion", [options.data], options.change);
}

function toggle(toShow, toHide, data, clickedActive, down) {
	var options = $.data(this, "ui-accordion").options;
	options.toShow = toShow;
	options.toHide = toHide;
	options.data = data;
	var complete = scopeCallback(completed, this);
	
	// count elements to animate
	options.running = toHide.size() == 0 ? toShow.size() : toHide.size();
	
	if ( options.animated ) {
		if ( !options.alwaysOpen && clickedActive ) {
			$.ui.accordion.animations[options.animated]({
				toShow: jQuery([]),
				toHide: toHide,
				complete: complete,
				down: down,
				autoheight: options.autoheight
			});
		} else {
			$.ui.accordion.animations[options.animated]({
				toShow: toShow,
				toHide: toHide,
				complete: complete,
				down: down,
				autoheight: options.autoheight
			});
		}
	} else {
		if ( !options.alwaysOpen && clickedActive ) {
			toShow.toggle();
		} else {
			toHide.hide();
			toShow.show();
		}
		complete(true);
	}
}

function clickHandler(event) {
	var options = $.data(this, "ui-accordion").options;
	if (options.disabled)
		return false;
	
	// called only when using activate(false) to close all parts programmatically
	if ( !event.target && !options.alwaysOpen ) {
		options.active.parent().andSelf().toggleClass(options.selectedClass);
		var toHide = options.active.next(),
			data = {
				instance: this,
				options: options,
				newHeader: jQuery([]),
				oldHeader: options.active,
				newContent: jQuery([]),
				oldContent: toHide
			},
			toShow = options.active = $([]);
		toggle.call(this, toShow, toHide, data );
		return false;
	}
	// get the click target
	var clicked = $(event.target);
	
	// due to the event delegation model, we have to check if one
	// of the parent elements is our actual header, and find that
	if ( clicked.parents(options.header).length )
		while ( !clicked.is(options.header) )
			clicked = clicked.parent();
	
	var clickedActive = clicked[0] == options.active[0];
	
	// if animations are still active, or the active header is the target, ignore click
	if (options.running || (options.alwaysOpen && clickedActive))
		return false;
	if (!clicked.is(options.header))
		return;

	// switch classes
	options.active.parent().andSelf().toggleClass(options.selectedClass);
	if ( !clickedActive ) {
		clicked.parent().andSelf().addClass(options.selectedClass);
	}

	// find elements to show and hide
	var toShow = clicked.next(),
		toHide = options.active.next(),
		//data = [clicked, options.active, toShow, toHide],
		data = {
			instance: this,
			options: options,
			newHeader: clicked,
			oldHeader: options.active,
			newContent: toShow,
			oldContent: toHide
		},
		down = options.headers.index( options.active[0] ) > options.headers.index( clicked[0] );
	
	options.active = clickedActive ? $([]) : clicked;
	toggle.call(this, toShow, toHide, data, clickedActive, down );

	return false;
};

function findActive(headers, selector) {
	return selector != undefined
		? typeof selector == "number"
			? headers.filter(":eq(" + selector + ")")
			: headers.not(headers.not(selector))
		: selector === false
			? $([])
			: headers.filter(":eq(0)");
}

$.extend($.ui.accordion, {
	defaults: {
		selectedClass: "selected",
		alwaysOpen: true,
		animated: 'slide',
		event: "click",
		header: "a",
		autoheight: true,
		running: 0,
		navigationFilter: function() {
			return this.href.toLowerCase() == location.href.toLowerCase();
		}
	},
	animations: {
		slide: function(options, additions) {
			options = $.extend({
				easing: "swing",
				duration: 300
			}, options, additions);
			if ( !options.toHide.size() ) {
				options.toShow.animate({height: "show"}, options);
				return;
			}
			var hideHeight = options.toHide.height(),
				showHeight = options.toShow.height(),
				difference = showHeight / hideHeight;
			options.toShow.css({ height: 0, overflow: 'hidden' }).show();
			options.toHide.filter(":hidden").each(options.complete).end().filter(":visible").animate({height:"hide"},{
				step: function(now) {
					var current = (hideHeight - now) * difference;
					if ($.browser.msie || $.browser.opera) {
						current = Math.ceil(current);
					}
					options.toShow.height( current );
				},
				duration: options.duration,
				easing: options.easing,
				complete: function() {
					if ( !options.autoheight ) {
						options.toShow.css("height", "auto");
					}
					options.complete();
				}
			});
		},
		bounceslide: function(options) {
			this.slide(options, {
				easing: options.down ? "bounceout" : "swing",
				duration: options.down ? 1000 : 200
			});
		},
		easeslide: function(options) {
			this.slide(options, {
				easing: "easeinout",
				duration: 700
			});
		}
	}
});

})(jQuery);
;(function($) {
	
	//If the UI scope is not available, add it
	$.ui = $.ui || {};

	$.fn.extend({
		dialog: function(options, data) {
			var args = Array.prototype.slice.call(arguments, 1);

			return this.each(function() {
				if (typeof options == "string") {
					var dialog = $.data(this, "ui-dialog") ||
						$.data($(this).parents(".ui-dialog:first").find(".ui-dialog-content")[0], "ui-dialog");
					dialog[options].apply(dialog, args);

				// INIT with optional options
				} else if (!$(this).is(".ui-dialog-content"))
					new $.ui.dialog(this, options);
			});
		}
	});

	$.ui.dialog = function(el, options) {
		
		this.options = options = $.extend({},
			$.ui.dialog.defaults,
			options && options.modal ? {resizable: false} : {},
			options);
		this.element = el;
		var self = this; //Do bindings

		$.data(this.element, "ui-dialog", this);
		
		$(el).bind("setData.dialog", function(event, key, value){
			options[key] = value;
		}).bind("getData.dialog", function(event, key){
			return options[key];
		});

		var uiDialogContent = $(el).addClass('ui-dialog-content');

		if (!uiDialogContent.parent().length) {
			uiDialogContent.appendTo('body');
		}
		uiDialogContent
			.wrap(document.createElement('div'))
			.wrap(document.createElement('div'));
		var uiDialogContainer = uiDialogContent.parent().addClass('ui-dialog-container').css({position: 'relative'});
		var uiDialog = this.uiDialog = uiDialogContainer.parent().hide()
			.addClass('ui-dialog')
			.css({position: 'absolute', width: options.width, height: options.height, overflow: 'hidden'}); 

		var classNames = uiDialogContent.attr('className').split(' ');

		// Add content classes to dialog, to inherit theme at top level of element
		$.each(classNames, function(i, className) {
			if (className != 'ui-dialog-content')
				uiDialog.addClass(className);
		});
		
		if (options.resizable && $.fn.resizable) {
			uiDialog.append('<div class="ui-resizable-n ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-s ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-e ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-w ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-ne ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-se ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-sw ui-resizable-handle"></div>')
				.append('<div class="ui-resizable-nw ui-resizable-handle"></div>');
			uiDialog.resizable({ maxWidth: options.maxWidth, maxHeight: options.maxHeight, minWidth: options.minWidth, minHeight: options.minHeight });
		}

		uiDialogContainer.prepend('<div class="ui-dialog-titlebar"></div>');
		var uiDialogTitlebar = $('.ui-dialog-titlebar', uiDialogContainer);
		var title = (options.title) ? options.title : (uiDialogContent.attr('title')) ? uiDialogContent.attr('title') : '';
		uiDialogTitlebar.append('<span class="ui-dialog-title">' + title + '</span>');
		uiDialogTitlebar.append('<a href="#" class="ui-dialog-titlebar-close"><span>X</span></a>');
		this.uiDialogTitlebarClose = $('.ui-dialog-titlebar-close', uiDialogTitlebar)
			.hover(function() { $(this).addClass('ui-dialog-titlebar-close-hover'); }, 
			       function() { $(this).removeClass('ui-dialog-titlebar-close-hover'); })
			.mousedown(function(ev) {
				ev.stopPropagation();
			})
			.click(function() {
				self.close();
				return false;
			})
			.keydown(function(ev) {
				var ESC = 27;
				ev.keyCode && ev.keyCode == ESC && self.close(); 
			});

		var l = 0;
		$.each(options.buttons, function() { l = 1; return false; });
		if (l == 1) {
			uiDialog.append('<div class="ui-dialog-buttonpane"></div>');
			var uiDialogButtonPane = $('.ui-dialog-buttonpane', uiDialog);
			$.each(options.buttons, function(name, value) {
				var btn = $(document.createElement('button')).text(name).click(value);
				uiDialogButtonPane.append(btn);
			});
		}
	
		if (options.draggable && $.fn.draggable) {
			uiDialog.draggable({
				handle: '.ui-dialog-titlebar',
				start: function() {
					self.activate();
				}
			});
		}
		uiDialog.mousedown(function() {
			self.activate();
		});
		uiDialogTitlebar.click(function() {
			self.activate();
		});
		
		// TODO: determine if this is necessary for modal dialogs
		options.bgiframe && $.fn.bgiframe && uiDialog.bgiframe();
		
		this.open = function() {
			options.modal && overlay.show(self, options.overlay);
			uiDialog.appendTo('body');
			var wnd = $(window), doc = $(document), top = doc.scrollTop(), left = doc.scrollLeft();
			if (options.position.constructor == Array) {
				// [x, y]
				top += options.position[1];
				left += options.position[0];
			} else {
				switch (options.position) {
					case 'center':
						top += (wnd.height() / 2) - (uiDialog.height() / 2);
						left += (wnd.width() / 2) - (uiDialog.width() / 2);
						break;
					case 'top':
						top += 0;
						left += (wnd.width() / 2) - (uiDialog.width() / 2);
						break;
					case 'right':
						top += (wnd.height() / 2) - (uiDialog.height() / 2);
						left += (wnd.width()) - (uiDialog.width());
						break;
					case 'bottom':
						top += (wnd.height()) - (uiDialog.height());
						left += (wnd.width() / 2) - (uiDialog.width() / 2);
						break;
					case 'left':
						top += (wnd.height() / 2) - (uiDialog.height() / 2);
						left += 0;
						break;
					default:
						//center
						top += (wnd.height() / 2) - (uiDialog.height() / 2);
						left += (wnd.width() / 2) - (uiDialog.width() / 2);
				}
			}
			top = top < doc.scrollTop() ? doc.scrollTop() : top;
			uiDialog.css({top: top, left: left});
			uiDialog.show();
			self.activate();

			// CALLBACK: open
			var openEV = null;
			var openUI = {
				options: options
			};
			this.uiDialogTitlebarClose.focus();
			$(this.element).triggerHandler("dialogopen", [openEV, openUI], options.open);
		};

		this.activate = function() {
			var maxZ = initZ = 0;
			$('.ui-dialog:visible').each(function() {
				maxZ = Math.max(maxZ, parseInt($(this).css("z-index"), 10) || initZ);
			});
			overlay.$el && overlay.$el.css('z-index', ++maxZ);
			uiDialog.css("z-index", ++maxZ);
		};

		this.close = function() {
			options.modal && overlay.hide();
			uiDialog.hide();

			// CALLBACK: close
			var closeEV = null;
			var closeUI = {
				options: options
			};
			$(this.element).triggerHandler("dialogclose", [closeEV, closeUI], options.close);
		};
		
		if (options.autoOpen)
			this.open();
	};
	
	$.extend($.ui.dialog, {
		defaults: {
			autoOpen: true,
			bgiframe: false,
			buttons: [],
			draggable: true,
			height: 200,
			minHeight: 100,
			minWidth: 150,
			modal: false,
			overlay: {},
			position: 'center',
			resizable: true,
			width: 300
		}
	});
	
	// This is a port of relevant pieces of Mike Alsup's blockUI plugin (http://www.malsup.com/jquery/block/)
	// duplicated here for minimal overlay functionality and no dependency on a non-UI plugin
	var overlay = {
		$el: null,
		events: $.map('focus,mousedown,mouseup,keydown,keypress,click'.split(','),
			function(e) { return e + '.ui-dialog-overlay'; }).join(' '),
		
		show: function(dialog, css) {
			if (this.$el) return;
			
			this.dialog = dialog;
			this.selects = this.ie6 && $('select:visible').css('visibility', 'hidden');
			var width = this.width();
			var height = this.height();
			this.$el = $('<div/>').appendTo(document.body)
				.addClass('ui-dialog-overlay').css($.extend({
					borderWidth: 0, margin: 0, padding: 0,
					position: 'absolute', top: 0, left: 0,
					width: width,
					height: height
				}, css));
			
			// prevent use of anchors and inputs
			$('a, :input').bind(this.events, function() {
				if ($(this).parents('.ui-dialog').length == 0) {
					dialog.uiDialogTitlebarClose.focus();
					return false;
				}
			});
			
			// allow closing by pressing the escape key
			$(document).bind('keydown.ui-dialog-overlay', function(e) {
				var ESC = 27;
				e.keyCode && e.keyCode == ESC && dialog.close(); 
			});
			
			// handle window resizing
			$overlay = this.$el;
			function resize() {
				// If the dialog is draggable and the user drags it past the
				// right edge of the window, the document becomes wider so we
				// need to stretch the overlay.  If the user then drags the
				// dialog back to the left, the document will become narrower,
				// so we need to shrink the overlay to the appropriate size.
				// This is handled by resetting the overlay to its original
				// size before setting it to the full document size.
				$overlay.css({
					width: width,
					height: height
				}).css({
					width: overlay.width(),
					height: overlay.height()
				});
			};
			$(window).bind('resize.ui-dialog-overlay', resize);
			dialog.uiDialog.is('.ui-draggable') && dialog.uiDialog.data('stop.draggable', resize);
			dialog.uiDialog.is('.ui-resizable') && dialog.uiDialog.data('stop.resizable', resize);
		},
		
		hide: function() {
			$('a, :input').add([document, window]).unbind('.ui-dialog-overlay');
			this.ie6 && this.selects.css('visibility', 'visible');
			this.$el = null;
			$('.ui-dialog-overlay').remove();
		},
		
		height: function() {
			var height;
			if (this.ie6
				// body is smaller than window
				&& ($(document.body).height() < $(window).height())
				// dialog is above the fold
				&& !(document.documentElement.scrollTop
					|| (this.dialog.uiDialog.offset().top
						+ this.dialog.uiDialog.height())
						> $(window).height())) {
				height = $(window).height();
			} else {
				height = $(document).height();
			}
			return height + 'px';
		},
		
		width: function() {
			var width;
			if (this.ie6
				// body is smaller than window
				&& ($(document.body).width() < $(window).width())
				// dialog is off to the right
				&& !(document.documentElement.scrollLeft
					|| (this.dialog.uiDialog.offset().left
						+ this.dialog.uiDialog.width())
						> $(window).width())) {
				width = $(window).width();
			} else {
				width = $(document).width();
			}
			return width + 'px';
		},
		
		// IE 6 compatibility
		ie6: $.browser.msie && $.browser.version < 7,
		selects: null
	};

})(jQuery);
(function($) {

	$.fn.extend({
		slider: function(options) {
			var args = Array.prototype.slice.call(arguments, 1);
			
			if ( options == "value" )
				return $.data(this[0], "ui-slider").value(arguments[1]);
			
			return this.each(function() {
				if (typeof options == "string") {
					var slider = $.data(this, "ui-slider");
					slider[options].apply(slider, args);

				} else if(!$.data(this, "ui-slider"))
					new $.ui.slider(this, options);
			});
		}
	});
	
	$.ui.slider = function(element, options) {

		//Initialize needed constants
		var self = this;
		this.element = $(element);
		$.data(element, "ui-slider", this);
		this.element.addClass("ui-slider");
		
		//Prepare the passed options
		this.options = $.extend({}, options);
		var o = this.options;
		$.extend(o, {
			axis: o.axis || (element.offsetWidth < element.offsetHeight ? 'vertical' : 'horizontal'),
			maxValue: !isNaN(parseInt(o.maxValue,10)) ? parseInt(o.maxValue,10) :  100,
			minValue: parseInt(o.minValue,10) || 0,
			startValue: parseInt(o.startValue,10) || 'none'		
		});
		
		//Prepare the real maxValue
		o.realMaxValue = o.maxValue - o.minValue;
		
		//Calculate stepping based on steps
		o.stepping = parseInt(o.stepping,10) || (o.steps ? o.realMaxValue/o.steps : 0);
		
		$(element).bind("setData.slider", function(event, key, value){
			self.options[key] = value;
		}).bind("getData.slider", function(event, key){
			return self.options[key];
		});

		//Initialize mouse and key events for interaction
		this.handle = o.handle ? $(o.handle, element) : $('> *', element);
		$(this.handle)
			.mouseInteraction({
				executor: this,
				delay: o.delay,
				distance: o.distance || 0,
				dragPrevention: o.prevention ? o.prevention.toLowerCase().split(',') : ['input','textarea','button','select','option'],
				start: this.start,
				stop: this.stop,
				drag: this.drag,
				condition: function(e, handle) {
					if(!this.disabled) {
						if(this.currentHandle) this.blur(this.currentHandle);
						this.focus(handle,1);
						return !this.disabled;
					}
				}
			})
			.wrap('<a href="javascript:void(0)"></a>')
			.parent()
				.bind('focus', function(e) { self.focus(this.firstChild); })
				.bind('blur', function(e) { self.blur(this.firstChild); })
				.bind('keydown', function(e) {
					if(/(37|39)/.test(e.keyCode))
						self.moveTo((e.keyCode == 37 ? '-' : '+')+'='+(self.options.stepping ? self.options.stepping : (self.options.realMaxValue / self.size)*5),this.firstChild);
				})
		;
		
		//Position the node
		if(o.helper == 'original' && (this.element.css('position') == 'static' || this.element.css('position') == '')) this.element.css('position', 'relative');
		
		//Prepare dynamic properties for later use
		if(o.axis == 'horizontal') {
			this.size = this.element.outerWidth();
			this.properties = ['left', 'width'];
		} else {
			this.size = this.element.outerHeight();
			this.properties = ['top', 'height'];
		}
		
		//Bind the click to the slider itself
		this.element.bind('click', function(e) { self.click.apply(self, [e]); });
		
		//Move the first handle to the startValue
		if(!isNaN(o.startValue)) this.moveTo(o.startValue, 0);
		
		//If we only have one handle, set the previous handle to this one to allow clicking before selecting the handle
		if(this.handle.length == 1) this.previousHandle = this.handle;
		
		
		if(this.handle.length == 2 && o.range) this.createRange();
	
	};
	
	$.extend($.ui.slider.prototype, {
		plugins: {},
		createRange: function() {
			this.rangeElement = $('<div></div>')
				.addClass('ui-slider-range')
				.css({ position: 'absolute' })
				.css(this.properties[0], parseInt($(this.handle[0]).css(this.properties[0]),10) + this.handleSize(0)/2)
				.css(this.properties[1], parseInt($(this.handle[1]).css(this.properties[0]),10) - parseInt($(this.handle[0]).css(this.properties[0]),10))
				.appendTo(this.element);
		},
		updateRange: function() {
				this.rangeElement.css(this.properties[0], parseInt($(this.handle[0]).css(this.properties[0]),10) + this.handleSize(0)/2);
				this.rangeElement.css(this.properties[1], parseInt($(this.handle[1]).css(this.properties[0]),10) - parseInt($(this.handle[0]).css(this.properties[0]),10));
		},
		getRange: function() {
			return this.rangeElement ? this.convertValue(parseInt(this.rangeElement.css(this.properties[1]),10)) : null;
		},
		ui: function(e) {
			return {
				instance: this,
				options: this.options,
				handle: this.currentHandle,
				value: this.value(),
				range: this.getRange()
			};
		},
		propagate: function(n,e) {
			$.ui.plugin.call(this, n, [e, this.ui()]);
			this.element.triggerHandler(n == "slide" ? n : "slide"+n, [e, this.ui()], this.options[n]);
		},
		destroy: function() {
			this.element
				.removeClass("ui-slider ui-slider-disabled")
				.removeData("ul-slider")
				.unbind(".slider");
			this.handles.removeMouseInteraction();
		},
		enable: function() {
			this.element.removeClass("ui-slider-disabled");
			this.disabled = false;
		},
		disable: function() {
			this.element.addClass("ui-slider-disabled");
			this.disabled = true;
		},
		focus: function(handle,hard) {
			this.currentHandle = $(handle).addClass('ui-slider-handle-active');
			if(hard) this.currentHandle.parent()[0].focus();
		},
		blur: function(handle) {
			$(handle).removeClass('ui-slider-handle-active');
			if(this.currentHandle && this.currentHandle[0] == handle) { this.previousHandle = this.currentHandle; this.currentHandle = null; };
		},
		value: function(handle) {
			if(this.handle.length == 1) this.currentHandle = this.handle;
			return ((parseInt($(handle != undefined ? this.handle[handle] || handle : this.currentHandle).css(this.properties[0]),10) / (this.size - this.handleSize())) * this.options.realMaxValue) + this.options.minValue;
		},
		convertValue: function(value) {
			return (value / (this.size - this.handleSize())) * this.options.realMaxValue;
		},
		translateValue: function(value) {
			return ((value - this.options.minValue) / this.options.realMaxValue) * (this.size - this.handleSize());
		},
		handleSize: function(handle) {
			return $(handle != undefined ? this.handle[handle] : this.currentHandle)['outer'+this.properties[1].substr(0,1).toUpperCase()+this.properties[1].substr(1)]();	
		},
		click: function(e) {
		
			// This method is only used if:
			// - The user didn't click a handle
			// - The Slider is not disabled
			// - There is a current, or previous selected handle (otherwise we wouldn't know which one to move)
			var pointer = [e.pageX,e.pageY];
			var clickedHandle = false; this.handle.each(function() { if(this == e.target) clickedHandle = true;  });
			if(clickedHandle || this.disabled || !(this.currentHandle || this.previousHandle)) return;

			//If a previous handle was focussed, focus it again
			if(this.previousHandle) this.focus(this.previousHandle, 1);
			
			//Move focussed handle to the clicked position
			this.offset = this.element.offset();
			this.moveTo(this.convertValue(e[this.properties[0] == 'top' ? 'pageY' : 'pageX'] - this.offset[this.properties[0]] - this.handleSize()/2));
			
		},
		start: function(e, handle) {
			
			var o = this.options;
			
			this.offset = this.element.offset();
			this.handleOffset = this.currentHandle.offset();
			this.clickOffset = { top: e.pageY - this.handleOffset.top, left: e.pageX - this.handleOffset.left };
			this.firstValue = this.value();
			
			this.propagate('start', e);
			return false;
						
		},
		stop: function(e) {
			this.propagate('stop', e);
			if(this.firstValue != this.value()) this.propagate('change', e);
			return false;
		},
		drag: function(e, handle) {

			var o = this.options;
			var position = { top: e.pageY - this.offset.top - this.clickOffset.top, left: e.pageX - this.offset.left - this.clickOffset.left};

			var modifier = position[this.properties[0]];			
			if(modifier >= this.size - this.handleSize()) modifier = this.size - this.handleSize();
			if(modifier <= 0) modifier = 0;
			
			if(o.stepping) {
				var value = this.convertValue(modifier);
				value = Math.round(value / o.stepping) * o.stepping;
				modifier = this.translateValue(value);	
			}

			if(this.rangeElement) {
				if(this.currentHandle[0] == this.handle[0] && modifier >= this.translateValue(this.value(1))) modifier = this.translateValue(this.value(1));
				if(this.currentHandle[0] == this.handle[1] && modifier <= this.translateValue(this.value(0))) modifier = this.translateValue(this.value(0));
			}	
			
			this.currentHandle.css(this.properties[0], modifier);
			if(this.rangeElement) this.updateRange();
			this.propagate('slide', e);
			return false;
			
		},
		moveTo: function(value, handle) {

			var o = this.options;
			if(handle == undefined && !this.currentHandle && this.handle.length != 1) return false; //If no handle has been passed, no current handle is available and we have multiple handles, return false
			if(handle == undefined && !this.currentHandle) handle = 0; //If only one handle is available, use it
			if(handle != undefined) this.currentHandle = this.previousHandle = $(this.handle[handle] || handle);

			if(value.constructor == String) value = /\-\=/.test(value) ? this.value() - parseInt(value.replace('-=', ''),10) : this.value() + parseInt(value.replace('+=', ''),10);
			if(o.stepping) value = Math.round(value / o.stepping) * o.stepping;
			value = this.translateValue(value);

			if(value >= this.size - this.handleSize()) value = this.size - this.handleSize();
			if(value <= 0) value = 0;
			if(this.rangeElement) {
				if(this.currentHandle[0] == this.handle[0] && value >= this.translateValue(this.value(1))) value = this.translateValue(this.value(1));
				if(this.currentHandle[0] == this.handle[1] && value <= this.translateValue(this.value(0))) value = this.translateValue(this.value(0));
			}
			
			this.currentHandle.css(this.properties[0], value);
			if(this.rangeElement) this.updateRange();
			
			this.propagate('start', null);
			this.propagate('stop', null);
			this.propagate('change', null);

		}
	});

})(jQuery);/*
 * Tabs 3 - New Wave Tabs
 *
 * Copyright (c) 2007 Klaus Hartl (stilbuero.de)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 * http://docs.jquery.com/UI/Tabs
 */

(function($) {

    // if the UI scope is not availalable, add it
    $.ui = $.ui || {};

    // tabs API methods
    $.fn.tabs = function() {
        var method = typeof arguments[0] == 'string' && arguments[0];
        var args = method && Array.prototype.slice.call(arguments, 1) || arguments;

        return this.each(function() {
            if (method) {
                var tabs = $.data(this, 'ui-tabs');
                tabs[method].apply(tabs, args);
            } else
                new $.ui.tabs(this, args[0] || {});
        });
    };

    // tabs class
    $.ui.tabs = function(el, options) {
        var self = this;

        this.element = el;

        this.options = $.extend({

            // basic setup
            selected: 0,
            unselect: options.selected === null,
            event: 'click',
            disabled: [],
            cookie: null, // pass options object as expected by cookie plugin: { expires: 7, path: '/', domain: 'jquery.com', secure: true }
            // TODO bookmarkable: $.ajaxHistory ? true : false,

            // Ajax
            spinner: 'Loading&#8230;',
            cache: false,
            idPrefix: 'ui-tabs-',
            ajaxOptions: {},

            // animations
            fx: null, /* e.g. { height: 'toggle', opacity: 'toggle', duration: 200 } */

            // templates
            tabTemplate: '<li><a href="#{href}"><span>#{label}</span></a></li>',
            panelTemplate: '<div></div>',

            // CSS classes
            navClass: 'ui-tabs-nav',
            selectedClass: 'ui-tabs-selected',
            unselectClass: 'ui-tabs-unselect',
            disabledClass: 'ui-tabs-disabled',
            panelClass: 'ui-tabs-panel',
            hideClass: 'ui-tabs-hide',
            loadingClass: 'ui-tabs-loading'

        }, options);

        this.options.event += '.ui-tabs'; // namespace event
        this.options.cookie = $.cookie && $.cookie.constructor == Function && this.options.cookie;

        $(el).bind('setData.ui-tabs', function(event, key, value) {
            self.options[key] = value;
            this.tabify();
        }).bind('getData.ui-tabs', function(event, key) {
            return self.options[key];
        });

        // save instance for later
        $.data(el, 'ui-tabs', this);

        // create tabs
        this.tabify(true);
    };

    // instance methods
    $.extend($.ui.tabs.prototype, {
        tabId: function(a) {
            return a.title && a.title.replace(/\s/g, '_').replace(/[^A-Za-z0-9\-_:\.]/g, '')
                || this.options.idPrefix + $.data(a);
        },
        ui: function(tab, panel) {
            return {
                instance: this,
                options: this.options,
                tab: tab,
                panel: panel
            };
        },
        tabify: function(init) {

            this.$lis = $('li:has(a[href])', this.element);
            this.$tabs = this.$lis.map(function() { return $('a', this)[0]; });
            this.$panels = $([]);

            var self = this, o = this.options;

            this.$tabs.each(function(i, a) {
                // inline tab
                if (a.hash && a.hash.replace('#', '')) // Safari 2 reports '#' for an empty hash
                    self.$panels = self.$panels.add(a.hash);
                // remote tab
                else if ($(a).attr('href') != '#') { // prevent loading the page itself if href is just "#"
                    $.data(a, 'href.ui-tabs', a.href); // required for restore on destroy
                    $.data(a, 'load.ui-tabs', a.href); // mutable
                    var id = self.tabId(a);
                    a.href = '#' + id;
                    var $panel = $('#' + id);
                    if (!$panel.length) {
                        $panel = $(o.panelTemplate).attr('id', id).addClass(o.panelClass)
                            .insertAfter( self.$panels[i - 1] || self.element );
                        $panel.data('destroy.ui-tabs', true);
                    }
                    self.$panels = self.$panels.add( $panel );
                }
                // invalid tab href
                else
                    o.disabled.push(i + 1);
            });

            if (init) {

                // attach necessary classes for styling if not present
                $(this.element).hasClass(o.navClass) || $(this.element).addClass(o.navClass);
                this.$panels.each(function() {
                    var $this = $(this);
                    $this.hasClass(o.panelClass) || $this.addClass(o.panelClass);
                });

                // disabled tabs
                for (var i = 0, index; index = o.disabled[i]; i++)
                    this.disable(index);

                // Try to retrieve selected tab:
                // 1. from fragment identifier in url if present
                // 2. from cookie
                // 3. from selected class attribute on <li>
                // 4. otherwise use given "selected" option
                // 5. check if tab is disabled
                this.$tabs.each(function(i, a) {
                    if (location.hash) {
                        if (a.hash == location.hash) {
                            o.selected = i;
                            // prevent page scroll to fragment
                            //if (($.browser.msie || $.browser.opera) && !o.remote) {
                            if ($.browser.msie || $.browser.opera) {
                                var $toShow = $(location.hash), toShowId = $toShow.attr('id');
                                $toShow.attr('id', '');
                                setTimeout(function() {
                                    $toShow.attr('id', toShowId); // restore id
                                }, 500);
                            }
                            scrollTo(0, 0);
                            return false; // break
                        }
                    } else if (o.cookie) {
                        var index = parseInt($.cookie('ui-tabs' + $.data(self.element)),10);
                        if (index && self.$tabs[index]) {
                            o.selected = index;
                            return false; // break
                        }
                    } else if ( self.$lis.eq(i).hasClass(o.selectedClass) ) {
                        o.selected = i;
                        return false; // break
                    }
                });
                var n = this.$lis.length;
                while (this.$lis.eq(o.selected).hasClass(o.disabledClass) && n) {
                    o.selected = ++o.selected < this.$lis.length ? o.selected : 0;
                    n--;
                }
                if (!n) // all tabs disabled, set option unselect to true
                    o.unselect = true;

                // highlight selected tab
                this.$panels.addClass(o.hideClass);
                this.$lis.removeClass(o.selectedClass);
                if (!o.unselect) {
                    this.$panels.eq(o.selected).show().removeClass(o.hideClass); // use show and remove class to show in any case no matter how it has been hidden before
                    this.$lis.eq(o.selected).addClass(o.selectedClass);
                }

                // load if remote tab
                var href = !o.unselect && $.data(this.$tabs[o.selected], 'load.ui-tabs');
                if (href)
                    this.load(o.selected, href);

                // disable click if event is configured to something else
                if (!(/^click/).test(o.event))
                    this.$tabs.bind('click', function(e) { e.preventDefault(); });

            }

            var hideFx, showFx, baseFx = { 'min-width': 0, duration: 1 }, baseDuration = 'normal';
            if (o.fx && o.fx.constructor == Array)
                hideFx = o.fx[0] || baseFx, showFx = o.fx[1] || baseFx;
            else
                hideFx = showFx = o.fx || baseFx;

            // reset some styles to maintain print style sheets etc.
            var resetCSS = { display: '', overflow: '', height: '' };
            if (!$.browser.msie) // not in IE to prevent ClearType font issue
                resetCSS.opacity = '';

            // Hide a tab, animation prevents browser scrolling to fragment,
            // $show is optional.
            function hideTab(clicked, $hide, $show) {
                $hide.animate(hideFx, hideFx.duration || baseDuration, function() { //
                    $hide.addClass(o.hideClass).css(resetCSS); // maintain flexible height and accessibility in print etc.
                    if ($.browser.msie && hideFx.opacity)
                        $hide[0].style.filter = '';
                    if ($show)
                        showTab(clicked, $show, $hide);
                });
            }

            // Show a tab, animation prevents browser scrolling to fragment,
            // $hide is optional.
            function showTab(clicked, $show, $hide) {
                if (showFx === baseFx)
                    $show.css('display', 'block'); // prevent occasionally occuring flicker in Firefox cause by gap between showing and hiding the tab panels
                $show.animate(showFx, showFx.duration || baseDuration, function() {
                    $show.removeClass(o.hideClass).css(resetCSS); // maintain flexible height and accessibility in print etc.
                    if ($.browser.msie && showFx.opacity)
                        $show[0].style.filter = '';

                    // callback
                    $(self.element).triggerHandler("show.ui-tabs", [self.ui(clicked, $show[0])]);

                });
            }

            // switch a tab
            function switchTab(clicked, $li, $hide, $show) {
                /*if (o.bookmarkable && trueClick) { // add to history only if true click occured, not a triggered click
                    $.ajaxHistory.update(clicked.hash);
                }*/
                $li.addClass(o.selectedClass)
                    .siblings().removeClass(o.selectedClass);
                hideTab(clicked, $hide, $show);
            }

            // attach tab event handler, unbind to avoid duplicates from former tabifying...
            this.$tabs.unbind(o.event).bind(o.event, function() {

                //var trueClick = e.clientX; // add to history only if true click occured, not a triggered click
                var $li = $(this).parents('li:eq(0)'),
                    $hide = self.$panels.filter(':visible'),
                    $show = $(this.hash);

                // If tab is already selected and not unselectable or tab disabled or click callback returns false stop here.
                // Check if click handler returns false last so that it is not executed for a disabled tab!
                if (($li.hasClass(o.selectedClass) && !o.unselect) || $li.hasClass(o.disabledClass)
                    || $(self.element).triggerHandler("select.ui-tabs", [self.ui(this, $show[0])]) === false) {
                    this.blur();
                    return false;
                }

                self.options.selected = self.$tabs.index(this);

                // if tab may be closed
                if (o.unselect) {
                    if ($li.hasClass(o.selectedClass)) {
                        self.options.selected = null;
                        $li.removeClass(o.selectedClass);
                        self.$panels.stop();
                        hideTab(this, $hide);
                        this.blur();
                        return false;
                    } else if (!$hide.length) {
                        self.$panels.stop();
                        var a = this;
                        self.load(self.$tabs.index(this), function() {
                            $li.addClass(o.selectedClass).addClass(o.unselectClass);
                            showTab(a, $show);
                        });
                        this.blur();
                        return false;
                    }
                }

                if (o.cookie)
                    $.cookie('ui-tabs' + $.data(self.element), self.options.selected, o.cookie);

                // stop possibly running animations
                self.$panels.stop();

                // show new tab
                if ($show.length) {

                    // prevent scrollbar scrolling to 0 and than back in IE7, happens only if bookmarking/history is enabled
                    /*if ($.browser.msie && o.bookmarkable) {
                        var showId = this.hash.replace('#', '');
                        $show.attr('id', '');
                        setTimeout(function() {
                            $show.attr('id', showId); // restore id
                        }, 0);
                    }*/

                    var a = this;
                    self.load(self.$tabs.index(this), function() {
                        switchTab(a, $li, $hide, $show);
                    });

                    // Set scrollbar to saved position - need to use timeout with 0 to prevent browser scroll to target of hash
                    /*var scrollX = window.pageXOffset || document.documentElement && document.documentElement.scrollLeft || document.body.scrollLeft || 0;
                    var scrollY = window.pageYOffset || document.documentElement && document.documentElement.scrollTop || document.body.scrollTop || 0;
                    setTimeout(function() {
                        scrollTo(scrollX, scrollY);
                    }, 0);*/

                } else
                    throw 'jQuery UI Tabs: Mismatching fragment identifier.';

                // Prevent IE from keeping other link focussed when using the back button
                // and remove dotted border from clicked link. This is controlled in modern
                // browsers via CSS, also blur removes focus from address bar in Firefox
                // which can become a usability and annoying problem with tabsRotate.
                if ($.browser.msie)
                    this.blur();

                //return o.bookmarkable && !!trueClick; // convert trueClick == undefined to Boolean required in IE
                return false;

            });

        },
        add: function(url, label, index) {
            if (url && label) {
                index = index || this.$tabs.length; // append by default

                var o = this.options;
                var $li = $(o.tabTemplate.replace(/#\{href\}/, url).replace(/#\{label\}/, label));
                $li.data('destroy.ui-tabs', true);

                var id = url.indexOf('#') == 0 ? url.replace('#', '') : this.tabId( $('a:first-child', $li)[0] );

                // try to find an existing element before creating a new one
                var $panel = $('#' + id);
                if (!$panel.length) {
                    $panel = $(o.panelTemplate).attr('id', id)
                        .addClass(o.panelClass).addClass(o.hideClass);
                    $panel.data('destroy.ui-tabs', true);
                }
                if (index >= this.$lis.length) {
                    $li.appendTo(this.element);
                    $panel.appendTo(this.element.parentNode);
                } else {
                    $li.insertBefore(this.$lis[index]);
                    $panel.insertBefore(this.$panels[index]);
                }

                this.tabify();

                if (this.$tabs.length == 1) {
                     $li.addClass(o.selectedClass);
                     $panel.removeClass(o.hideClass);
                     var href = $.data(this.$tabs[0], 'load.ui-tabs');
                     if (href)
                         this.load(index, href);
                }

                // callback
                $(this.element).triggerHandler("add.ui-tabs",
                    [this.ui(this.$tabs[index], this.$panels[index])]
                );

            } else
                throw 'jQuery UI Tabs: Not enough arguments to add tab.';
        },
        remove: function(index) {
            if (index && index.constructor == Number) {
                var o = this.options, $li = this.$lis.eq(index).remove(),
                    $panel = this.$panels.eq(index).remove();

                // If selected tab was removed focus tab to the right or
                // tab to the left if last tab was removed.
                if ($li.hasClass(o.selectedClass) && this.$tabs.length > 1)
                    this.click(index + (index < this.$tabs.length ? 1 : -1));
                this.tabify();

                // callback
                $(this.element).triggerHandler("remove.ui-tabs",
                    [this.ui($li.find('a')[0], $panel[0])]
                );

            }
        },
        enable: function(index) {
            var self = this, o = this.options, $li = this.$lis.eq(index);
            $li.removeClass(o.disabledClass);
            if ($.browser.safari) { // fix disappearing tab (that used opacity indicating disabling) after enabling in Safari 2...
                $li.css('display', 'inline-block');
                setTimeout(function() {
                    $li.css('display', 'block');
                }, 0);
            }

            o.disabled = $.map(this.$lis.filter('.' + o.disabledClass),
                function(n, i) { return self.$lis.index(n); } );

            // callback
            $(this.element).triggerHandler("enable.ui-tabs",
                [this.ui(this.$tabs[index], this.$panels[index])]
            );

        },
        disable: function(index) {
            var self = this, o = this.options;
            this.$lis.eq(index).addClass(o.disabledClass);

            o.disabled = $.map(this.$lis.filter('.' + o.disabledClass),
                function(n, i) { return self.$lis.index(n); } );

            // callback
            $(this.element).triggerHandler("disable.ui-tabs",
                [this.ui(this.$tabs[index], this.$panels[index])]
            );

        },
        select: function(index) {
            if (typeof index == 'string')
                index = this.$tabs.index( this.$tabs.filter('[href$=' + index + ']')[0] );
            this.$tabs.eq(index).trigger(this.options.event);
        },
        load: function(index, callback) { // callback is for internal usage only
            var self = this, o = this.options,
                $a = this.$tabs.eq(index), a = $a[0];

            var url = $a.data('load.ui-tabs');

            // no remote - just finish with callback
            if (!url) {
                typeof callback == 'function' && callback();
                return;
            }

            // load remote from here on
            if (o.spinner) {
                var $span = $('span', a), label = $span.html();
                $span.html('<em>' + o.spinner + '</em>');
            }
            var finish = function() {
                self.$tabs.filter('.' + o.loadingClass).each(function() {
                    $(this).removeClass(o.loadingClass);
                    if (o.spinner)
                        $('span', this).html(label);
                });
                self.xhr = null;
            };
            var ajaxOptions = $.extend({}, o.ajaxOptions, {
                url: url,
                success: function(r, s) {
                    $(a.hash).html(r);
                    finish();
                    // This callback is required because the switch has to take
                    // place after loading has completed.
                    typeof callback == 'function' && callback();

                    if (o.cache)
                        $.removeData(a, 'load.ui-tabs'); // if loaded once do not load them again

                    // callback
                    $(self.element).triggerHandler("load.ui-tabs",
                        [self.ui(self.$tabs[index], self.$panels[index])]
                    );

                    o.ajaxOptions.success && o.ajaxOptions.success(r, s);
                }
            });
            if (this.xhr) {
                // terminate pending requests from other tabs and restore tab label
                this.xhr.abort();
                finish();
            }
            $a.addClass(o.loadingClass);
            setTimeout(function() { // timeout is again required in IE, "wait" for id being restored
                self.xhr = $.ajax(ajaxOptions);
            }, 0);

        },
        url: function(index, url) {
            this.$tabs.eq(index).data('load.ui-tabs', url);
        },
        destroy: function() {
            var o = this.options;
            $(this.element).unbind('.ui-tabs')
                .removeClass(o.navClass).removeData('ui-tabs');
            this.$tabs.each(function() {
                var href = $.data(this, 'href.ui-tabs');
                if (href)
                    this.href = href;
                $(this).unbind('.ui-tabs')
                    .removeData('href.ui-tabs').removeData('load.ui-tabs');
            });
            this.$lis.add(this.$panels).each(function() {
                if ($.data(this, 'destroy.ui-tabs'))
                    $(this).remove();
                else
                    $(this).removeClass([o.selectedClass, o.unselectClass,
                        o.disabledClass, o.panelClass, o.hideClass].join(' '));
            });
        }
    });

})(jQuery);
/*
 * Tabs 3 extensions
 *
 * Copyright (c) 2007 Klaus Hartl (stilbuero.de)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 * http://docs.jquery.com/UI/TabsExtensions
 */
 
(function($) {
    
    /*
     * Rotate
     */
    $.extend($.ui.tabs.prototype, {
        rotation: null,
        rotate: function(ms) {
            var self = this;
            function stop(e) {
                if (e.clientX) { // only in case of a true click
                    clearInterval(self.rotation);
                }
            }
            // start interval
            if (ms) {
                var t = this.options.selected;
                this.rotation = setInterval(function() {
                    t = ++t < self.$tabs.length ? t : 0;
                    self.click(t);
                }, ms);
                this.$tabs.bind(this.options.event, stop);
            }
            // stop interval
            else {
                clearInterval(this.rotation);
                this.$tabs.unbind(this.options.event, stop);
            }
        }
    });

})(jQuery);