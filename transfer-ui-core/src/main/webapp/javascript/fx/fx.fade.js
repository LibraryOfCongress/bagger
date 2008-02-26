(function($) {
  
  $.ec.fade = function(o) {

    return this.queue(function() {
      
      // Create element
      var el = $(this), props = ['opacity'];
      
      // Set options
      var mode = $.ec.setMode(el, o.options.mode || 'effect'); // Set Mode
      if (mode == 'toggle') mode = el.is(':hidden') ? 'show' : 'hide'; // Set for toggle
      var opacity = o.options.opacity || 0; // Default fade opacity
      
      // Adjust
      $.ec.save(el, props); el.show(); // Save & Show
      if(mode == 'show') el.css({opacity: 0}); // Shift
      
      // Animation
      var animation = {opacity: mode == 'show' ? 1 : opacity};
      
      // Animate
      el.animate(animation, { queue: false, duration: o.duration, easing: o.options.easing, complete: function() {
        if(mode == 'hide') el.hide(); // Hide
        if(mode == 'hide') $.ec.restore(el, props); // Restore
        if(o.callback) o.callback.apply(this, arguments); // Callback
        el.dequeue();
      }});
      
    });
    
  };
  
})(jQuery);