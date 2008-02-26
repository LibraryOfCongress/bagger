(function($) {
  
  $.ec.clip = function(o) {

    return this.queue(function() {

      // Create element
      var el = $(this), props = ['position','top','left','width','height'];
      
      // Set options
      var mode = $.ec.setMode(el, o.options.mode || 'hide'); // Set Mode
      var direction = o.options.direction || 'vertical'; // Default direction
      
      // Adjust
      $.ec.save(el, props); el.show(); // Save & Show
      $.ec.createWrapper(el).css({overflow:'hidden'}); // Create Wrapper
      var ref = {
        size: (direction == 'vertical') ? 'height' : 'width',
        position: (direction == 'vertical') ? 'top' : 'left'
      };
      var distance = (direction == 'vertical') ? el.height() : el.width();
      if(mode == 'show') { el.css(ref.size, 0); el.css(ref.position, distance / 2); } // Shift
      
      // Animation
      var animation = {};
      animation[ref.size] = mode == 'show' ? distance : 0;
      animation[ref.position] = mode == 'show' ? 0 : distance / 2;
        
      // Animate
      el.animate(animation, { queue: false, duration: o.duration, easing: o.options.easing, complete: function() {
        if(mode == 'hide') el.hide(); // Hide
        $.ec.restore(el, props); $.ec.removeWrapper(el); // Restore
        if(o.callback) o.callback.apply(this, arguments); // Callback
        el.dequeue();
      }}); 
      
    });
    
  };
  
})(jQuery);
