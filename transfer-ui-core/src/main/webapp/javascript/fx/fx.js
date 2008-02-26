(function($) {

  $.ec = $.ec || {}; //Add the 'ec' scope

  $.extend($.ec, {
    save: function(el, set) {
      for(var i=0;i<set.length;i++) {
        if(set[i] !== null) $.data(el[0], "ec.storage."+set[i], el.css(set[i]));
      }
    },
    restore: function(el, set) {
      for(var i=0;i<set.length;i++) {
        if(set[i] !== null) el.css(set[i], $.data(el[0], "ec.storage."+set[i]));
      }
    },
    setMode: function(el, mode) {
      if (mode == 'toggle') mode = el.is(':hidden') ? 'show' : 'hide'; // Set for toggle
      return mode;
    },
    getBaseline: function(origin, original) { // Translates a [top,left] array into a baseline value
      // this should be a little more flexible in the future to handle a string & hash
      var y, x;
      switch (origin[0]) {
        case 'top': y = 0; break;
        case 'middle': y = 0.5; break;
        case 'bottom': y = 1; break;
        default: y = origin[0] / original.height;
      };
      switch (origin[1]) {
        case 'left': x = 0; break;
        case 'center': x = 0.5; break;
        case 'right': x = 1; break;
        default: x = origin[1] / original.width;
      };
      return {x: x, y: y};
    },
    createWrapper: function(el) {
      if (el.parent().attr('id') == 'fxWrapper')
        return el;
      var props = {width: el.outerWidth({margin:true}), height: el.outerHeight({margin:true}), 'float': el.css('float')};
      el.wrap('<div id="fxWrapper"></div>');
      var wrapper = el.parent();
      if (el.css('position') == 'static'){
        wrapper.css({position: 'relative'});
        el.css({position: 'relative'});
      } else {
        var top = parseInt(el.css('top'), 10); if (top.constructor != Number) top = 'auto';
        var left = parseInt(el.css('left'), 10); if (left.constructor != Number) left = 'auto';
        wrapper.css({ position: el.css('position'), top: top, left: left, zIndex: el.css('z-index') }).show();
        el.css({position: 'relative', top:0, left:0});
      }
      wrapper.css(props);
      return wrapper;
    },
    removeWrapper: function(el) {
      if (el.parent().attr('id') == 'fxWrapper')
        return el.parent().replaceWith(el);
      return el;
    },
    setTransition: function(el, list, factor, val) {
      val = val || {};
      $.each(list,function(i, x){
        unit = el.cssUnit(x);
        if (unit[0] > 0) val[x] = unit[0] * factor + unit[1];
      });
      return val;
    },
    animateClass: function(value, duration, easing, callback) {

      var cb = (typeof easing == "function" ? easing : (callback ? callback : null));
      var ea = (typeof easing == "object" ? easing : null);

      this.each(function() {

        var offset = {}; var that = $(this); var oldStyleAttr = that.attr("style") || '';
        if(typeof oldStyleAttr == 'object') oldStyleAttr = oldStyleAttr["cssText"]; /* Stupidly in IE, style is a object.. */
        if(value.toggle) { that.hasClass(value.toggle) ? value.remove = value.toggle : value.add = value.toggle; }

        //Let's get a style offset
        var oldStyle = $.extend({}, (document.defaultView ? document.defaultView.getComputedStyle(this,null) : this.currentStyle));
        if(value.add) that.addClass(value.add); if(value.remove) that.removeClass(value.remove);
        var newStyle = $.extend({}, (document.defaultView ? document.defaultView.getComputedStyle(this,null) : this.currentStyle));
        if(value.add) that.removeClass(value.add); if(value.remove) that.addClass(value.remove);

        // The main function to form the object for animation
        for(var n in newStyle) {
          if( typeof newStyle[n] != "function" && newStyle[n] /* No functions and null properties */
          && n.indexOf("Moz") == -1 && n.indexOf("length") == -1 /* No mozilla spezific render properties. */
          && newStyle[n] != oldStyle[n] /* Only values that have changed are used for the animation */
          && (n.match(/color/i) || (!n.match(/color/i) && !isNaN(parseInt(newStyle[n],10)))) /* Only things that can be parsed to integers or colors */
          && (oldStyle.position != "static" || (oldStyle.position == "static" && !n.match(/left|top|bottom|right/))) /* No need for positions when dealing with static positions */
          ) offset[n] = newStyle[n];
        }

        that.animate(offset, duration, ea, function() { // Animate the newly constructed offset object
          // Change style attribute back to original. For stupid IE, we need to clear the damn object.
          if(typeof $(this).attr("style") == 'object') { $(this).attr("style")["cssText"] = ""; $(this).attr("style")["cssText"] = oldStyleAttr; } else $(this).attr("style", oldStyleAttr);
          if(value.add) $(this).addClass(value.add); if(value.remove) $(this).removeClass(value.remove);
          if(cb) cb.apply(this, arguments);
        });

      });
    }
  });

  //Extend the methods of jQuery
  $.fn.extend({
    //Save old methods
    _show: $.fn.show,
    _hide: $.fn.hide,
    _toggle: $.fn.toggle,
    _addClass: $.fn.addClass,
    _removeClass: $.fn.removeClass,
    _toggleClass: $.fn.toggleClass,
    // New ec methods
    effect: function(fx,o,speed,callback) {
      return $.ec[fx] ? $.ec[fx].call(this, {method: fx, options: o || {}, duration: speed, callback: callback }) : null;
    },
    show: function() {
      if(!arguments[0] || (arguments[0].constructor == Number || /(slow|fast)/.test(arguments[0])))
        return this._show.apply(this, arguments);
      else {
        var o = arguments[1] || {}; o['mode'] = 'show';
        return this.effect.apply(this, [arguments[0], o, arguments[2] || o.duration, arguments[3] || o.callback]);
      }
    },
    hide: function() {
      if(!arguments[0] || (arguments[0].constructor == Number || /(slow|fast)/.test(arguments[0])))
        return this._hide.apply(this, arguments);
      else {
        var o = arguments[1] || {}; o['mode'] = 'hide';
        return this.effect.apply(this, [arguments[0], o, arguments[2] || o.duration, arguments[3] || o.callback]);
      }
    },
    toggle: function(){
      if(!arguments[0] || (arguments[0].constructor == Number || /(slow|fast)/.test(arguments[0])))
        return this._toggle.apply(this, arguments);
      else {
        var o = arguments[1] || {}; o['mode'] = 'toggle';
        return this.effect.apply(this, [arguments[0], o, arguments[2] || o.duration, arguments[3] || o.callback]);
      }
    },
    addClass: function(classNames,speed,easing,callback) {
      return speed ? $.ec.animateClass.apply(this, [{ add: classNames },speed,easing,callback]) : this._addClass(classNames);
    },
    removeClass: function(classNames,speed,easing,callback) {
      return speed ? $.ec.animateClass.apply(this, [{ remove: classNames },speed,easing,callback]) : this._removeClass(classNames);
    },
    toggleClass: function(classNames,speed,easing,callback) {
      return speed ? $.ec.animateClass.apply(this, [{ toggle: classNames },speed,easing,callback]) : this._toggleClass(classNames);
    },
    morph: function(remove,add,speed,easing,callback) {
      return $.ec.animateClass.apply(this, [{ add: add, remove: remove },speed,easing,callback]);
    },
    switchClass: function() {
      this.morph.apply(this, arguments);
    },
    // helper functions
    cssUnit: function(key) {
      var style = this.css(key), val = [];
      $.each( ['em','px','%','pt'], function(i, unit){
        if(style.indexOf(unit) > 0)
          val = [parseFloat(style), unit];
      });
      return val;
    }
  });
  
  /*
   * jQuery Color Animations
   * Copyright 2007 John Resig
   * Released under the MIT and GPL licenses.
   */

    // We override the animation for all of these color styles
    jQuery.each(['backgroundColor', 'borderBottomColor', 'borderLeftColor', 'borderRightColor', 'borderTopColor', 'color', 'outlineColor'], function(i,attr){
        jQuery.fx.step[attr] = function(fx){
            if ( fx.state == 0 ) {
                fx.start = getColor( fx.elem, attr );
                fx.end = getRGB( fx.end );
            }

            fx.elem.style[attr] = "rgb(" + [
                Math.max(Math.min( parseInt((fx.pos * (fx.end[0] - fx.start[0])) + fx.start[0]), 255), 0),
                Math.max(Math.min( parseInt((fx.pos * (fx.end[1] - fx.start[1])) + fx.start[1]), 255), 0),
                Math.max(Math.min( parseInt((fx.pos * (fx.end[2] - fx.start[2])) + fx.start[2]), 255), 0)
            ].join(",") + ")";
        }
    });

    // Color Conversion functions from highlightFade
    // By Blair Mitchelmore
    // http://jquery.offput.ca/highlightFade/

    // Parse strings looking for color tuples [255,255,255]
    function getRGB(color) {
        var result;

        // Check if we're already dealing with an array of colors
        if ( color && color.constructor == Array && color.length == 3 )
            return color;

        // Look for rgb(num,num,num)
        if (result = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(color))
            return [parseInt(result[1]), parseInt(result[2]), parseInt(result[3])];

        // Look for rgb(num%,num%,num%)
        if (result = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(color))
            return [parseFloat(result[1])*2.55, parseFloat(result[2])*2.55, parseFloat(result[3])*2.55];

        // Look for #a0b1c2
        if (result = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(color))
            return [parseInt(result[1],16), parseInt(result[2],16), parseInt(result[3],16)];

        // Look for #fff
        if (result = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(color))
            return [parseInt(result[1]+result[1],16), parseInt(result[2]+result[2],16), parseInt(result[3]+result[3],16)];

        // Look for rgba(0, 0, 0, 0) == transparent in Safari 3
        if (result = /rgba\(0, 0, 0, 0\)/.exec(color))
            return colors['transparent']

        // Otherwise, we're most likely dealing with a named color
        return colors[jQuery.trim(color).toLowerCase()];
    }

    function getColor(elem, attr) {
        var color;

        do {
            color = jQuery.curCSS(elem, attr);

            // Keep going until we find an element that has color, or we hit the body
            if ( color != '' && color != 'transparent' || jQuery.nodeName(elem, "body") )
                break;

            attr = "backgroundColor";
        } while ( elem = elem.parentNode );

        return getRGB(color);
    };

    // Some named colors to work with
    // From Interface by Stefan Petre
    // http://interface.eyecon.ro/

    var colors = {
        aqua:[0,255,255],
        azure:[240,255,255],
        beige:[245,245,220],
        black:[0,0,0],
        blue:[0,0,255],
        brown:[165,42,42],
        cyan:[0,255,255],
        darkblue:[0,0,139],
        darkcyan:[0,139,139],
        darkgrey:[169,169,169],
        darkgreen:[0,100,0],
        darkkhaki:[189,183,107],
        darkmagenta:[139,0,139],
        darkolivegreen:[85,107,47],
        darkorange:[255,140,0],
        darkorchid:[153,50,204],
        darkred:[139,0,0],
        darksalmon:[233,150,122],
        darkviolet:[148,0,211],
        fuchsia:[255,0,255],
        gold:[255,215,0],
        green:[0,128,0],
        indigo:[75,0,130],
        khaki:[240,230,140],
        lightblue:[173,216,230],
        lightcyan:[224,255,255],
        lightgreen:[144,238,144],
        lightgrey:[211,211,211],
        lightpink:[255,182,193],
        lightyellow:[255,255,224],
        lime:[0,255,0],
        magenta:[255,0,255],
        maroon:[128,0,0],
        navy:[0,0,128],
        olive:[128,128,0],
        orange:[255,165,0],
        pink:[255,192,203],
        purple:[128,0,128],
        violet:[128,0,128],
        red:[255,0,0],
        silver:[192,192,192],
        white:[255,255,255],
        yellow:[255,255,0],
        transparent: [255,255,255]
    };

})(jQuery);