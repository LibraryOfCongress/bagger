(function($) {
  
  $.ec.shake = function(o) {

    return this.queue(function() {

      // Create element
      var el = $(this), props = ['position','top','left'];
      
      // Set options
      var mode = $.ec.setMode(el, o.options.mode || 'effect'); // Set Mode
      var direction = o.options.direction || 'left'; // Default direction
      var distance = o.options.distance || 20; // Default distance
      var times = o.options.times || 3; // Default # of times
      var speed = o.duration || o.options.duration || 140; // Default speed per shake
      
      // Adjust
      $.ec.save(el, props); el.show(); // Save & Show
      $.ec.createWrapper(el); // Create Wrapper
      var ref = (direction == 'up' || direction == 'down') ? 'top' : 'left';
      var motion = (direction == 'up' || direction == 'left') ? 'pos' : 'neg';
      
      // Animation
      var animation = {}, animation1 = {}, animation2 = {};
      animation[ref] = (motion == 'pos' ? '-=' : '+=')  + distance;
      animation1[ref] = (motion == 'pos' ? '+=' : '-=')  + distance * 2;
      animation2[ref] = (motion == 'pos' ? '-=' : '+=')  + distance * 2;
      
      // Animate
      el.animate(animation, speed, o.options.easing);
      for (var i = 1; i < times; i++) { // Shakes
        el.animate(animation1, speed, o.options.easing).animate(animation2, speed, o.options.easing);
      };
      el.animate(animation1, speed, o.options.easing).
      animate(animation, speed / 2, o.options.easing, function(){ // Last shake
        $.ec.restore(el, props); $.ec.removeWrapper(el); // Restore
        if(o.callback) o.callback.apply(this, arguments); // Callback
      });
      el.queue('fx', function() { el.dequeue(); });
      el.dequeue();
    });
    
  };
  
})(jQuery);
