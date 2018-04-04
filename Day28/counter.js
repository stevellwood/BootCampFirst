// Wait until all elements are rendered before taking action
$(document).ready(function() {
  var text_box = $("#counter_text");
  var inc_button = $("#increase");  
  var dec_button = $("#decrease");
  var reset_button = $("#reset");
  var dec_amt = $("#dec_amt");
  var inc_amt = $("#inc_amt");
  var pro_check = $("#toggle_pro");
  var zero_message = $("#zero_message");

  var is_pro = false;    
  var curr_num = Number(text_box.val());
  
  display();  

  console.log("Num is", curr_num);

  // When the increase button is clicked
  inc_button.click(function() {
    var inc = Number(inc_amt.val());
    curr_num += inc;
    console.log("After increasing", curr_num);
    console.log("Increase amount", inc);
    display();
  });    

  // When the decrease button is clicked
  dec_button.click(function() {
    var dec = Number(dec_amt.val());
    curr_num -= dec;
    console.log("After decreasing", curr_num);
    console.log("Decrease amount", dec);
    display();
  }); 

  // When the reset button is clicked
  reset_button.click(function() {
    curr_num=0;
    console.log("Reset the counter to 0");
    display();
  });

  pro_check.click(function() {
    console.log("Clicked the checkbox");
    
    if(is_pro == false) {
      // 0 to 20 ==> -10 to 10
      var rand_dec = Math.floor(Math.random() * 21) - 10;
      var rand_inc = Math.floor(Math.random() * 21) - 10;
      
      dec_amt.val(rand_dec);
      inc_amt.val(rand_inc);
      is_pro = true;
    } else {
      dec_amt.val(1);
      inc_amt.val(1);
      is_pro = false;
    }
  });

  function display() {
    if(curr_num === 0) {
      zero_message.show();
    } else {
      zero_message.hide();
    }

    divisibleByTwo();
    checkSeven();
    aboveEight();
    text_box.val(curr_num);
  }

  // If the number contains a 7, display it in green
  function checkSeven() {
    var num_as_string = curr_num.toString();

    if(num_as_string.indexOf("7") != -1) {
      text_box.addClass("has_seven");
    } else {
      text_box.removeClass("has_seven");
    }
  }

  // If the number is even, italicize it
  function divisibleByTwo() {
    if((curr_num % 2) === 0) {
      text_box.addClass("divisible_by_two");
      console.log("Divisible by two - should be in italics!");
    } else {
      text_box.removeClass("divisible_by_two");
    }
  }

  // If the number > 8, display it in red
  function aboveEight() {
    if(curr_num > 8) {
      text_box.addClass("above_eight");  
      console.log("Above 8 - should be red!");
    } else {
      text_box.removeClass("above_eight");
      console.log("At 8 or below - should be normal!");
    }
  }
});