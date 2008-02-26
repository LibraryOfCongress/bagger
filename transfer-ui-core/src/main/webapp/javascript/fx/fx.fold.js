(function($) {
  
  $.ec.fold = function(o) {

    return this.queue(function() {

      // Create element
      var el = $(this), props = ['position'];
      
      // Set options
      var mode = $.ec.setMode(el, o.options.mode || 'hide'); // Set Mode
      var size = o.options.size || 15; // Default fold size
      
      // Adjust
      $.ec.save(el, props); el.show(); // Save & Show
      var wrapper = $.ec.createWrapper(el).css({overflow:'hidden'}); // Create Wrapper
      var ref = (mode == 'show') ? ['width', 'height'] : ['height', 'width'];
      var distance = (mode == 'show') ? [wrapper.width(), wrapper.height()] : [wrapper.height(), wrapper.width()];
      if(mode == 'show') wrapper.css({height: size, width: 0}); // Shift
      
      // Animation
      var animation1 = {}, animation2 = {};
      animation1[ref[0]] = mode == 'show' ? distance[0] : size;
      animation2[ref[1]] = mode == 'show' ? distance[1] : 0;
      
      // Animate
      wrapper.animate(animation1, o.duration / 2, o.options.easing)
      .animate(animation2, o.duration / 2, o.options.easing, function() {
        if(mode == 'hide') el.hide(); // Hide
        $.ec.restore(el, props); $.ec.removeWrapper(el); // Restore
        if(o.callback) o.callback.apply(this, arguments); // Callback
        el.dequeue();
      });
      
    });
    
  };
  
})(jQuery);