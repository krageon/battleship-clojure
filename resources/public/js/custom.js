$(document).ready(function() {
  $("input").click(function(){
    if($(this).val() == "~"){
      $(this).val("o");
    }else{
      $(this).val("~");
    }
  });
});