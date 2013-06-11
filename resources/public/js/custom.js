$(document).ready(function() {
  $currentShip = "";
  var letters = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
  var destroyer = 1;
  var submarine = 1;

  $('form#shotboard input').click(function(e){
    var button = {xy: $(this).attr('name')};
    $.post("/shoot", button,function(data, success){
      if(success){
        window.location.assign("/shoot");
      };
    });
    return false;
  })

  $('#fleeter').submit(function(){
    var canSubmit = true;
    var counter = 0;
    $('tr td#amount').each(function(){
      if($(this).text().charAt(0) != 0){
        canSubmit = false;
      }
    });
    if(canSubmit){
      $('input[value="o"]').each(function(){
        counter++;
      });
    }else{
      alert("You haven't placed your whole fleet yet!"); return false;
    }
    if(counter === 18){
      var aircraftcarrier = getShip("Aircraft Carrier", 0);
      var battleship = getShip("Battleship", 0);
      var cruiser = getShip("Cruiser", 0);
      var destroyer1 = getShip("Destroyer", 1);
      var destroyer2 = getShip("Destroyer", 2);
      var submarine1 = getShip("Submarine", 1);
      var submarine2 = getShip("Submarine", 2);

      /* Check for objects
      console.log(aircraftcarrier);
      console.log(battleship);
      console.log(cruiser);
      console.log(destroyer1); console.log(destroyer2);
      console.log(submarine1); console.log(submarine2);
      */

      $.post("/ships", aircraftcarrier, function(data, success){
        if(success){
          $.post("/ships", battleship, function(data, success){
            if(success){
              $.post("/ships", cruiser, function(data,success){
                if(success){
                  $.post("/ships", destroyer1, function(data, success){
                    if(success){
                      $.post("/ships", destroyer2, function(data, success){
                        if(success){
                          $.post("/ships", submarine1, function(data, success){
                            if(success){
                              $.post("/ships", submarine2, function(data, success){
                                if(success){
                                  window.location.assign('/play');
                                }
                              });
                            }
                          });
                        }
                      });
                    }
                  });
                }
              });
            }
          });
        }
      });
    }else{
      console.log("You bugged the system, good job, now try again");
      window.location.reload();
      history.go(0);
    }
    return false;
  });

  $(".add").click(function(){
    $row = $(this).closest('tr');
    $amount = $row.find('#amount').text().charAt(0);
    if($amount > 0){
      $row.find('#amount').text($amount-1+"x");
      $size = $row.find('#size').text();
      coord = getFirstCoord();
      var name = $row.find('#name').text();
      version = getId(name);

      for($i = 0; $i < $size; $i++){
        $currentShip = $('input[name="'+coord+($i+1)+'"]');
        setAttribute(name, version, "o", coord+"1", true, $size);
      }
    }else{
      alert("You've placed the max amount of this ship");
    }
  });

  $("table#fleet td input").click(function(){
    if($(this).val() === 'o'){
      var size = $(this).attr('size');
      if(size > 1){
        var yCoord= $(this).attr('start').charAt(0);
        var xCoord= $(this).attr('start').charAt(1);
        var posArray = $.inArray(yCoord, letters);
        var name = $(this).attr('ship');
        var version = $(this).attr('version');
        var size = $(this).attr('size');
        var horizontal = $(this).attr('horizontal') == 'true';
        var canSet = true;

        if(horizontal){
          if((parseInt(posArray)+parseInt(size)) > letters.length){ alert('Cannot change direction, there is no space!');}
          else{
            for(i = 1; i < size+1; i++){
              if($('input[name="'+letters[(posArray+i)]+xCoord+'"]').val() === "o"){
                alert('Cannot change direction, there is already a ship in place');
                canSet = false; break;return false;
              }
            }
            if(canSet){
              removeShipByName(name, version);
              for(i = 0; i < size; i++){
                $currentShip = $('input[name="'+letters[(parseInt(posArray)+i)]+xCoord+'"]');
                setAttribute(name, version, "o", yCoord+xCoord, false, size);
              }
            }
          }
        }else{
          if(parseInt(xCoord)+(size-1) > 10){ alert('Cannot change direction, there is no space!');}
          else{
            for(i = 1; i < size+1; i++){
              if($('input[name="'+yCoord+(parseInt(xCoord)+i)+'"]').val() === "o"){
                alert('Cannot change direction, there is already a ship in place');
                canSet = false; break;return false;
              }
            }
            if(canSet){
              removeShipByName(name, version)
              for(i = 0; i < size; i++){
                $currentShip = $('input[name="'+yCoord+(parseInt(xCoord)+i)+'"]');
                setAttribute(name, version, "o", yCoord+xCoord, true, size);
              }
            }
          }
        }
      }
    }else{
      var name =$currentShip.attr('ship');
      var version = $currentShip.attr('version');
      var size = $currentShip.attr('size');
      var horizontal = $currentShip.attr('horizontal') == 'true';
      buttonCoord = $(this).attr('name');
      var yButton = buttonCoord.charAt(0);
      if(buttonCoord.length > 2){
        var xButton = buttonCoord.charAt(1)+buttonCoord.charAt(2);
      }else{
        var xButton = buttonCoord.charAt(1);
      }
      $currentShip.attr('start', yButton+xButton);
      var posArray = $.inArray(yButton, letters);

      if(horizontal){
        if(isLegal(horizontal, size, yButton, xButton)){
          removeShipByName(name, version);
          for(i = 0; i < size; i++){
            $currentShip = $('input[name="'+yButton+(parseInt(xButton)+i)+'"]');
            setAttribute(name, version, "o", yButton+xButton, true, size);
          }
        }else{
          alert('There is not enough room to put the ship here');
        }
      }else{
        if(isLegal(horizontal, size, yButton, xButton)){
          removeShipByName(name, version);
          for(i = 0; i < size; i++){
            $currentShip = $('input[name="'+(letters[(parseInt(posArray)+i)]+xButton+'"]'));
            setAttribute(name, version, "o", yButton+xButton, false, size);
          }
        }else{
          alert('There is not enough room to put the ship here');
        }
      }
    }
  });

  function setAttribute(ship, version, value, startCoordination, horizontal, size){
    $currentShip.attr('ship', ship);
    $currentShip.attr('version', version);
    $currentShip.val(value);
    $currentShip.attr('start', startCoordination);
    $currentShip.attr('horizontal', horizontal);
    $currentShip.attr('size', size);
  }

  function getFirstCoord(){
    var isEmpty = true;
    for(a = 0; a < letters.length; a++){
      for(i = 1; i < 11; i++){
        if($('input[name="'+letters[a]+(i)+'"]').val() === "o"){
          isEmpty = false; break;
        }else{
          isEmpty = true;
        }
      }
      if(isEmpty){
        return letters[a];break;
      }
    }
  }

  function isLegal(horizontal, size, startY, startX){
    var isLegal = true;
    var posArray = $.inArray(startY, letters);

    if(horizontal){
      for(i = 0; i < size; i++){
        if($('input[name="'+startY+(parseInt(startX)+i)+'"]').val() === "o" || ((parseInt(startX))+(parseInt(size-1))) > 10){
          isLegal = false; break;
        }
      }
    }else{
      for(i = 0; i < size; i++){
        if($('input[name="'+letters[(parseInt(posArray)+i)]+startX+'"]').val() === "o" || ((parseInt(posArray))+(parseInt(size))) > letters.length){
          isLegal = false; break;
        }
      }
    }
    return isLegal;
  }

  function getShip(name, version){
    var xy ="";
    var horizontal = true;

    if(version === 0){
      xy = $('input[ship="'+name+'"]').attr("start");
      horizontal = $('input[ship="'+name+'"]').attr("horizontal") === "true";
    }else{
      $('input[ship="'+name+'"]').each(function(){
        if(parseInt($(this).attr('version'))===version){
          xy = $(this).attr('start');
          horizontal = $(this).attr('horizontal') === 'true';
        }
      });
    }
    var ship = {name: name, xy: xy, horizontal: horizontal};
    return ship;
  }

  function removeShipByName(name, version){
    $('input[ship="'+name+'"]').each(function(){
      if($(this).attr('version') === version){
        $currentShip = $(this);
        setAttribute("","", "~", "", false, 0);
      }
    });
  }

  function getId(name){
    var id = 0;
    if(name === 'Destroyer'){
      id = destroyer; destroyer++;
    }
    if(name === "Submarine"){
      id = submarine; submarine++;
    }
    return id;
  }
});