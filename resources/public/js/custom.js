/* TODO: instead of letting the user click, already place the boats and let them drag. */
$(document).ready(function() {
  var $maxSize = 18;
  var $total = 0;
  $("table#fleet td input").click(function(){
    if($(this).val() == "~"){
      if($total < $maxSize){
        $(this).val("o");
        $total++;
      }else{
        alert("You've already place the maximum of boats!!");
      }
    }else{
      $(this).val("~");
      $total--;
    }
  });

  $("#right-menu input[type='submit']").click(function(){
    alert("Aantal bootjes: "+$("td input[value='o']").length+ " and total: "+$total + " and max: "+$maxSize);
  });
});